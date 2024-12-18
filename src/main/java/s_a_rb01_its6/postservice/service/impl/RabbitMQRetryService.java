package s_a_rb01_its6.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RabbitMQRetryService{

    private static final String DLQ_NAME = "postUserDeleteQueue.dlq";
    private static final String MAIN_EXCHANGE = "userDeleteExchange";
    private static final String MAIN_ROUTING_KEY = "userDeleteKey";

    private final RabbitTemplate rabbitTemplate;

    // Scheduler polls the DLQ every minute
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void processFailedMessages() {
        // Retrieve messages from DLQ
        System.out.println("Checking DLQ for failed messages...");
        Object failedMessage = rabbitTemplate.receiveAndConvert(DLQ_NAME);

        if (failedMessage != null) {
            System.out.println("Re-publishing failed message: " + failedMessage);
            // Re-publish message to main exchange
            rabbitTemplate.convertAndSend(MAIN_EXCHANGE, MAIN_ROUTING_KEY, failedMessage);
        }
    }
}