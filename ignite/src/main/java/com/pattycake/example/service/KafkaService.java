package com.pattycake.example.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.BatchConsumerAwareMessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaService implements DisposableBean {

    private final KafkaProperties properties;
    private final Map<String, KafkaMessageListenerContainer> containerMap = new ConcurrentHashMap<>();
    private final KafkaListenerEndpointRegistrar registrar;


    public KafkaService(final KafkaProperties properties, final KafkaListenerEndpointRegistrar registrar) {
        this.properties = Objects.requireNonNull(properties);
        this.registrar = Objects.requireNonNull(registrar);
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 30_000L)
    public void connectToKafka() {

        final KafkaMessageListenerContainer container = createListenerContainer("quickstart-events", 0);
        container.start();


//        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
//        factory.setConsumerFactory(cf);
//        factory.set
//        factory.createListenerContainer()
//        new MessagingMessageConverter().
//        factory.setMessageConverter( new MessagingMessageConverter());
//
//        factory.contain
//
//
//        final MethodKafkaListenerEndpoint<String, Object> endpoint = new MethodKafkaListenerEndpoint<>();
//        endpoint.setTopicPartitions(new TopicPartitionOffset("quickstart-events", 0, TopicPartitionOffset.SeekPosition.BEGINNING));
//        endpoint.setConsumerProperties(toProperties(consumerProperties));
//        endpoint.setAutoStartup(true);
//        endpoint.setMessagingConverter();
//        endpoint
//
//        registrar.registerEndpoint(endpoint);

        log.info("Starting Kafka listener");
    }


    private KafkaMessageListenerContainer createListenerContainer(final String topic, final int partition) {
        final Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        final DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProperties);
        final ContainerProperties containerProperties = new ContainerProperties(new TopicPartitionOffset(topic, partition, TopicPartitionOffset.SeekPosition.BEGINNING));
        final KafkaMessageListenerContainer container = new KafkaMessageListenerContainer<>(cf, containerProperties);
        container.setupMessageListener(new MyBatchConsumerAwareMessageListener());

        containerMap.put(topic, container);
        return container;
    }


    private Properties toProperties(final Map<String, Object> properties) {
        final Properties props = new Properties();
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            props.put(entry.getKey(), entry.getValue());
        }
        return props;
    }

    @Override
    public void destroy() throws Exception {
        containerMap.values().stream().forEach(AbstractMessageListenerContainer::stop);
    }

    static class MyBatchConsumerAwareMessageListener implements BatchConsumerAwareMessageListener<String, Object> {

        @Override
        public void onMessage(final List<ConsumerRecord<String, Object>> data, final Consumer<?, ?> consumer) {
            log.error("Start batch");
            data.forEach(c -> log.error("consumerRecord[{}] consumer[{}]", c, consumer));
            log.error("End batch");
        }
    }
}
