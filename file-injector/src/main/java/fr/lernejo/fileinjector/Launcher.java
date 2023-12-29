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
            checkFilePathArgument(args);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                List<Map<String, Object>> gameDataList = objectMapper.readValue(new File(args[0]), new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> gameData : gameDataList) {
                    Message amqpMessage = MessageBuilder.withBody(objectMapper.writeValueAsBytes(gameData)).setContentType("application/json").setMessageId(UUID.randomUUID().toString()).setHeader("game_id", gameData.get("id").toString()).build();
                    rabbitTemplate.convertAndSend("game_info", amqpMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkFilePathArgument(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Missing file path argument");
        }
    }

}
