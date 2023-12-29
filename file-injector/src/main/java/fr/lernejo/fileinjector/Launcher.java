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
import java.util.UUID;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar your-jar-file.jar path/to/your/file.json");
            throw new IllegalArgumentException("Missing file path argument");
        }

        String filePath = args[0];

        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            // Load the file using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<GameInfo> gameInfoList = objectMapper.readValue(new File(filePath), new TypeReference<List<GameInfo>>() {});

            // Get RabbitTemplate bean
            RabbitTemplate rabbitTemplate = springContext.getBean(RabbitTemplate.class);

            int totalMessages = gameInfoList.size();
            int messagesSent = 0;

            // For each GameInfo, send it to the 'game_info' queue
            for (GameInfo gameInfo : gameInfoList) {
                String messageId = UUID.randomUUID().toString();
                String contentType = "application/json";

                // Convert the GameInfo to JSON bytes
                byte[] gameInfoBytes = objectMapper.writeValueAsBytes(gameInfo);

                // Build the AMQP message
                Message amqpMessage = MessageBuilder
                    .withBody(gameInfoBytes)
                    .setContentType(contentType)
                    .setMessageId(messageId)
                    .build();

                // Send the message to the 'game_info' queue
                rabbitTemplate.convertAndSend("game_info", amqpMessage);

                System.out.println("Sent message with ID: " + messageId);
                messagesSent++;

                // Check if all messages are sent
                if (messagesSent == totalMessages) {
                    System.out.println("All messages sent. Exiting the program.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
