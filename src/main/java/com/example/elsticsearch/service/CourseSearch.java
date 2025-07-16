package com.example.elsticsearch.service;

import com.example.elsticsearch.document.CourseDocument;
import com.example.elsticsearch.model.SearchParams;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearch {

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    @Qualifier("elasticsearchRestHighLevelClient") // Ensure bean is defined in your config
    private RestHighLevelClient restHighLevelClient;

    public SearchHits<CourseDocument> searchCourses(SearchParams params) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Full-text search on title and description
        if (StringUtils.hasText(params.getQ())) {
            BoolQueryBuilder fuzzyQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("title", params.getQ()).fuzziness("AUTO"))
                    .should(QueryBuilders.matchQuery("description", params.getQ()));
            boolQuery.must(fuzzyQuery);
        }

        // Filter by category
        if (StringUtils.hasText(params.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category.keyword", params.getCategory()));
        }

        // Filter by type
        if (StringUtils.hasText(params.getType())) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", params.getType()));
        }

        // Age Range filter
        if (params.getMinAge() != null || params.getMaxAge() != null) {
            RangeQueryBuilder ageRange = QueryBuilders.rangeQuery("minAge");
            if (params.getMinAge() != null) ageRange.gte(params.getMinAge());
            if (params.getMaxAge() != null) ageRange.lte(params.getMaxAge());
            boolQuery.filter(ageRange);
        }

        // Price Range filter
        if (params.getMinPrice() != null || params.getMaxPrice() != null) {
            RangeQueryBuilder priceRange = QueryBuilders.rangeQuery("price");
            if (params.getMinPrice() != null) priceRange.gte(params.getMinPrice());
            if (params.getMaxPrice() != null) priceRange.lte(params.getMaxPrice());
            boolQuery.filter(priceRange);
        }

        // Start date filter
        if (params.getStartDate() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("nextSessionDate").gte(params.getStartDate().toString()));
        }

        // Sorting
        SortBuilder<?> sort = SortBuilders.fieldSort("nextSessionDate").order(SortOrder.ASC);
        if ("priceAsc".equalsIgnoreCase(params.getSort())) {
            sort = SortBuilders.fieldSort("price").order(SortOrder.ASC);
        } else if ("priceDesc".equalsIgnoreCase(params.getSort())) {
            sort = SortBuilders.fieldSort("price").order(SortOrder.DESC);
        }

        // Build final query with pagination
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSorts(sort)
                .withPageable(PageRequest.of(params.getPage(), params.getSize()))
                .build();

        return operations.search(query, CourseDocument.class);
    }

    // ✅ Autocomplete method using Completion Suggester
    public List<String> autocomplete(String partialTitle) throws IOException {
        SearchRequest searchRequest = new SearchRequest("courses");

        CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder("suggest")
                .prefix(partialTitle)
                .skipDuplicates(true)
                .size(10);

        SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion("course-suggest", suggestionBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .suggest(suggestBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Suggest suggest = response.getSuggest();
        if (suggest == null) return Collections.emptyList();

        return suggest.getSuggestion("course-suggest")
                .getEntries().stream()
                .flatMap(entry -> entry.getOptions().stream())
                .map(option -> option.getText().string())
                .collect(Collectors.toList());
    }
}
