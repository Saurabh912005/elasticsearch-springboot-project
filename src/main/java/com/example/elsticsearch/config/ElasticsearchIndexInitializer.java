package com.example.elsticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void createIndexIfNotExists() throws IOException {
        String indexName = "courses";

        boolean exists = elasticsearchClient.indices()
                .exists(e -> e.index(indexName))
                .value();

        if (!exists) {
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .mappings(m -> m
                            .properties("title", p -> p.text(t -> t))
                            .properties("suggest", p -> p.completion(cmp -> cmp))
                    )
            );
            System.out.println("✅ Created index: " + indexName);
        } else {
            System.out.println("ℹ️ Index already exists: " + indexName);
        }
    }
}
