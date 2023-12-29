package fr.lernejo.fileinjector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            RabbitTemplate rabbitTemplate = springContext.getBean(RabbitTemplate.class);
            String filePath = args.length == 0 ? null : args[0];
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                List<Map<String, Object>> gameDataList = objectMapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {});

                for (Map<String, Object> gameData : gameDataList) {
                    String messageId = UUID.randomUUID().toString();
                    String contentType = "application/json";
                    String gameId = gameData.get("id").toString();
                    byte[] gameDataBytes = objectMapper.writeValueAsBytes(gameData);
                    Message amqpMessage = MessageBuilder
                        .withBody(gameDataBytes)
                        .setContentType(contentType)
                        .setMessageId(messageId)
                        .setHeader("game_id", gameId)
                        .build();
                    rabbitTemplate.convertAndSend("game_info", amqpMessage);
                    System.out.println("Sent message with ID: " + messageId + " and game_id: " + gameId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
