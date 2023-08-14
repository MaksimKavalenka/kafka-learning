package org.learning.kafka.common;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Map;
import java.util.Properties;

public enum KafkaProperties {

    WORD_COUNT_STREAM("word-count-stream");

    private final Properties properties;

    KafkaProperties(String applicationId) {
        properties = new Properties();

        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, String.format("%s-client", applicationId));
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    public Properties getProperties() {
        return properties;
    }

    public Properties overrideProperties(Map<String, String> newProperties) {
        newProperties.forEach(properties::setProperty);
        return properties;
    }

}
