package com.example.elsticsearch.controller;

import com.example.elsticsearch.model.SearchParams;
import com.example.elsticsearch.service.CourseSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    @Autowired
    private CourseSearch searchService;

    // ✅ Main search endpoint with filters and sorting
    @GetMapping("/search")
    public Object search(@ModelAttribute SearchParams params) {
        try {
            return searchService.searchCourses(params);
        } catch (Exception e) {
            e.printStackTrace(); // Log to console
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ Autocomplete endpoint
    @GetMapping("/search/suggest")
    public Object suggest(@RequestParam String q) {
        try {
            List<String> suggestions = searchService.autocomplete(q);
            return Map.of("suggestions", suggestions);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("error", "Failed to fetch suggestions: " + e.getMessage());
        }
    }
}
