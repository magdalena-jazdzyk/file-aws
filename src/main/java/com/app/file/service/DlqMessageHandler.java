package com.app.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.app.file.constant.RabbitErrorConstant.DLX_QUEUE;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DlqMessageHandler {

    private final RabbitTemplate rabbitTemplate;
    private final String RETRIES_COUNT = "max_reties";


    @RabbitListener(queues = DLX_QUEUE)
    public void processFailedMessages(Message message) {
        Integer retries = (Integer) message.getMessageProperties()
                .getHeaders().get(RETRIES_COUNT);
        if (retries == null) {
            retries = 1;
        } else if (retries >= 3) {
            reportError(message);
            return;
        } else {
            retries++;
        }
        message.getMessageProperties()
                .getHeaders().put(RETRIES_COUNT, retries);
        rabbitTemplate.send(message.getMessageProperties().getReceivedRoutingKey(), message);
    }

    private void reportError(Message message) {
        log.error("Przekroczono maksymalną liczbę powtórek dla wiadomości: " +
                new String(message.getBody()) +
                ". Nagłówki: " + message.getMessageProperties().getHeaders());

    }


}
