package com.example.url_shortener.repo;

import com.example.url_shortener.entity.ClickEventDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ClickEventRepository extends MongoRepository<ClickEventDocument,String> {
    long countByShortCode(String shortCode);

    @Aggregation(pipeline = {
            "{ '$match': { 'shortCode': ?0 } }",
            "{ '$project': { 'date': { '$substr': ['$timestamp', 0, 10] } } }",
            "{ '$group': { '_id': '$date', 'count': { '$sum': 1 } } }"
    })
    List<Map<String, Object>> countClicksByDay(String shortCode);

    @Aggregation(pipeline = {
            "{ '$match': { 'shortCode': ?0 } }",
            "{ '$group': { '_id': '$referrer', 'count': { '$sum': 1 } } }",
            "{ '$sort': { 'count': -1 } }",
            "{ '$limit': 5 }"
    })
    List<Map<String, Object>> countClicksByReferrer(String shortCode);

    @Aggregation(pipeline = {
            "{ '$match': { 'shortCode': ?0 } }",
            "{ '$group': { '_id': '$browser', 'count': { '$sum': 1 } } }",
            "{ '$sort': { 'count': -1 } }",
            "{ '$limit': 5 }"
    })
    List<Map<String, Object>> countClicksByBrowser(String shortCode);
}
