package com.app.file.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.app.file.constant.RabbitErrorConstant.*;
import static com.app.file.constant.RabbitQueueConstant.*;


@Configuration
public class RabbitMQConfig {

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }


    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    public Queue uploadQueue() {
        return QueueBuilder.durable(UPLOAD_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .build();
    }

    @Bean
    public Queue deleteQueue() {
        return QueueBuilder.durable(DELETE_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .build();
    }

    @Bean
    public Queue restoreQueue() {
        return QueueBuilder.durable(RESTORE_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .build();
    }


    @Bean
    public Queue moveToTrashQueue() {
        return QueueBuilder.durable(MOVE_TO_TRASH_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .build();
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
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(realDeleteQueue()).to(dlxExchange()).with(REAL_DELETE_QUEUE);
    }

}


