package fr.lernejo.search.api;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GameSearchController {
    @Autowired
    private RestHighLevelClient elasticsearchClient;
    @GetMapping("/api/games")
    public List<Map<String, Object>> searchGames(@RequestParam String query, @RequestParam(defaultValue = "10") int size) {
        SearchRequest searchRequest = new SearchRequest("games");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.queryStringQuery(query)).size(size);
        try {
            SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            return Optional.ofNullable(searchResponse)
                .map(response -> Arrays.stream(response.getHits().getHits())
                    .map(SearchHit::getSourceAsMap)
                    .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
