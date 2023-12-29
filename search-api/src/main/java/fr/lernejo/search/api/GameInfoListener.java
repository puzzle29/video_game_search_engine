package fr.lernejo.search.api;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Map;

@Component
public class GameInfoListener {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public GameInfoListener(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(String message, @Header("game_id") String gameId, Message amqpMessage) {
        try {
            // Accessing message properties
            Map<String, Object> headers = amqpMessage.getMessageProperties().getHeaders();
            String contentType = headers.get("content_type").toString();

            // Index the document in the 'games' index with the provided game_id and content_type
            IndexRequest indexRequest = new IndexRequest("games")
                .id(gameId)
                .source(message, XContentType.JSON);
            // .setPipeline(contentType);  // Assuming content_type is a valid pipeline name


            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

            // Handle the response if needed
            // response.getResult();  // Get the result of the index operation
            // Handle the response if needed
            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                System.out.println("Document created successfully with ID: " + response.getId());
            } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                System.out.println("Document updated successfully with ID: " + response.getId());
            }

        } catch (Exception e) {
            // Handle exceptions (e.g., log or rethrow)
            e.printStackTrace();
        }
    }

}
