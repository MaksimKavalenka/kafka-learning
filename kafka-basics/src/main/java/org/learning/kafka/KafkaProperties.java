package org.learning.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public enum KafkaProperties {

    CONSUMER() {

        @Override
        public void setProperties(Properties properties) {
            properties.setProperty("key.deserializer", StringDeserializer.class.getName());
            properties.setProperty("value.deserializer", StringDeserializer.class.getName());

            properties.setProperty("group.id", "java-topic-consumer-group");
            properties.setProperty("auto.offset.reset", "earliest");
        }

    },

    PRODUCER() {

        @Override
        public void setProperties(Properties properties) {
            properties.setProperty("key.serializer", StringSerializer.class.getName());
            properties.setProperty("value.serializer", StringSerializer.class.getName());
        }

    };

    private final Properties properties;

    KafkaProperties() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");

        setProperties(properties);
    }

    public abstract void setProperties(Properties properties);

    public Properties getProperties() {
        return properties;
    }

}
