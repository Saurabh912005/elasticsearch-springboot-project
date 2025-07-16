package com.example.elsticsearch.loader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.example.elsticsearch.document.CourseDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseLoader implements CommandLineRunner {

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("➡️ Starting course data load...");

        Resource resource = new ClassPathResource("sample-courses.json");

        List<CourseDocument> courses = Arrays.asList(
                objectMapper.readValue(resource.getInputStream(), CourseDocument[].class)
        );

        System.out.println("➡️ Parsed " + courses.size() + " courses from JSON");

        List<BulkOperation> operations = courses.stream()
                .map(course -> BulkOperation.of(op -> op
                        .index(idx -> idx
                                .index("courses")
                                .id(course.getId())
                                .document(course)
                        )))
                .collect(Collectors.toList());

        BulkRequest request = new BulkRequest.Builder()
                .operations(operations)
                .build();

        BulkResponse response=elasticsearchClient.bulk(request);

// Log response status
        if (response.errors()) {
            System.out.println("❌ Bulk indexing had errors:");
            response.items().forEach(item -> {
                if (item.error() != null) {
                    System.out.println("❌ Error indexing ID: " + item.index());
                    System.out.println(item.error().reason());
                }
            });
        } else {
            System.out.println("✅ Successfully indexed " + response.items().size() + " courses");
        }

        System.out.println("✅ Indexed " + courses.size() + " courses to Elasticsearch");
    }





}
