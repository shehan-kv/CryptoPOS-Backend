package com.cryptopos.inventory.config;

import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitmqConfig {

    @Bean
    public AsyncRabbitTemplate asyncrabbitTemplate(RabbitTemplate rabbitTemplate) {
        return new AsyncRabbitTemplate(rabbitTemplate);
    }
}
