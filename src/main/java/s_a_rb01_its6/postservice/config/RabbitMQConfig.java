package s_a_rb01_its6.postservice.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    public static final String USER_DELETE_EXCHANGE = "userDeleteExchange";
    public static final String USER_DELETE_QUEUE = "postUserDeleteQueue";
    public static final String USER_DELETE_ROUTING_KEY = "userDeleteKey";

    public static final String USER_DELETE_EXCHANGE_DLQ = "userDeleteExchange.dlq";
    public static final String USER_DELETE_QUEUE_DLQ = "postUserDeleteQueue.dlq";
    public static final String USER_DELETE_ROUTING_KEY_DLQ = "userDeleteKey.dlq";

    @Bean
    public DirectExchange userDeleteExchange() {
        return new DirectExchange(USER_DELETE_EXCHANGE);
    }

    @Bean
    public Queue userDeleteQueue() {
        return QueueBuilder.durable(USER_DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", USER_DELETE_EXCHANGE_DLQ) // Route to DLX on failure
                .withArgument("x-dead-letter-routing-key", USER_DELETE_ROUTING_KEY_DLQ) // DLQ routing key
                .withArgument("x-message-ttl", 60000) // Retry after 60 seconds
                .build();
    }

    @Bean
    public Binding bindingDelete(Queue userDeleteQueue, DirectExchange userDeleteExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(userDeleteExchange).with(USER_DELETE_ROUTING_KEY);
    }

    @Bean
    public DirectExchange userDeleteExchangeDLQ() {
        return new DirectExchange(USER_DELETE_EXCHANGE_DLQ);
    }

    // DLQ
    @Bean
    public Queue userDeleteQueueDLQ() {
        return QueueBuilder.durable(USER_DELETE_QUEUE_DLQ).build();
    }

    // Binding for DLQ
    @Bean
    public Binding bindingDeleteDLQ(Queue userDeleteQueueDLQ, DirectExchange userDeleteExchangeDLQ) {
        return BindingBuilder.bind(userDeleteQueueDLQ).to(userDeleteExchangeDLQ).with(USER_DELETE_ROUTING_KEY_DLQ);
    }

    public static final String USER_UPDATE_EXCHANGE = "userUpdateExchange";
    public static final String USER_UPDATE_QUEUE = "postUserUpdateQueue";
    public static final String USER_UPDATE_ROUTING_KEY = "userUpdateKey";

    @Bean
    public DirectExchange userUpdateExchange() {
        return new DirectExchange(USER_UPDATE_EXCHANGE);
    }

    @Bean
    public Queue userUpdateQueue() {
        return new Queue(USER_UPDATE_QUEUE);
    }

    @Bean
    public Binding bindingUpdate(Queue userUpdateQueue, DirectExchange userUpdateExchange) {
        return BindingBuilder.bind(userUpdateQueue).to(userUpdateExchange).with(USER_UPDATE_ROUTING_KEY);
    }
}
