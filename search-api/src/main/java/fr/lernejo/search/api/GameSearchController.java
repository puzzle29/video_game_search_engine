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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GameSearchController {

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @GetMapping("/api/games")
    public List<Map<String, Object>> searchGames(@RequestParam String query, @RequestParam(defaultValue = "10") int size) {
        SearchRequest searchRequest = new SearchRequest("games");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(query));
        searchSourceBuilder.size(size);  // Définir la taille ici

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;

        try {
            searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (searchResponse != null) {
            return Arrays.stream(searchResponse.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .collect(Collectors.toList());
        } else {
            // Gérer le cas où searchResponse est null (par exemple, en renvoyant une réponse d'erreur appropriée)
            return Collections.emptyList(); // Ou une autre action appropriée
        }
    }
}
