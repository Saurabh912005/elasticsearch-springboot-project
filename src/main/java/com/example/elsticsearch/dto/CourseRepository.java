package com.example.elsticsearch.dto;

import com.example.elsticsearch.document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
    // You can define custom query methods here if needed
}
