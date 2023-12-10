package com.app.file.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {


    public static final String UPLOAD_QUEUE = "upload-queue";
    public static final String DELETE_QUEUE = "delete-queue";
    public static final String DELAYED_DELETE_QUEUE = "delayed-delete-queue";
    public static final String MOVE_TO_TRASH_QUEUE = "move-to_trash-queue";

    public static final String RESTORE_QUEUE = "restore-queue";

    public static final String HARD_DELETE_QUEUE = "hard-delete-queue";
    private static final String REAL_DELETE_QUEUE = "real-delete-queue";
    private static final String DLX_EXCHANGE = ""; // Pusty string oznacza domyślną wymianę
    private static final long TTL_30_DAYS = 2592000000L; // 30 dni w milisekundach


    @Bean
    public Queue uploadQueue() {
        return new Queue(UPLOAD_QUEUE);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(DELETE_QUEUE);
    }

    @Bean
    public Queue restoreQueue() {
        return new Queue(RESTORE_QUEUE);
    }


    @Bean
    public Queue moveToTrashQueue() {
        return new Queue(MOVE_TO_TRASH_QUEUE, true);
    }

    @Bean
    public Queue delayedDeleteQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", TTL_30_DAYS);
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", REAL_DELETE_QUEUE);
        return new Queue(DELAYED_DELETE_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue realDeleteQueue() {
        return new Queue(HARD_DELETE_QUEUE);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }




    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(realDeleteQueue()).to(dlxExchange()).with(REAL_DELETE_QUEUE);
    }

}
