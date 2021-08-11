package com.pattycake.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;

@Configuration
public class KakfaConfiguration {

    @Bean
    public KafkaListenerEndpointRegistrar kafkaListenerEndpointRegistrar() {
        return new KafkaListenerEndpointRegistrar();
    }
}
