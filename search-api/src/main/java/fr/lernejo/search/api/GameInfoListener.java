package fr.lernejo.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class GameInfoListener {
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GameInfoListener(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "game_info")
    public void onMessage(Message amqpMessage) {
        try {
            Map<String, Object> headers = amqpMessage.getMessageProperties().getHeaders();
            String gameId = headers.get("game_id").toString();
            String messageBody = new String(amqpMessage.getBody());
            Map<String, Object> gameData = objectMapper.readValue(messageBody, Map.class);
            IndexRequest indexRequest = new IndexRequest("games")
                .id(gameId)
                .source(messageBody, XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                System.out.println("Document created successfully with ID: " + response.getId());
            } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                System.out.println("Document updated successfully with ID: " + response.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
