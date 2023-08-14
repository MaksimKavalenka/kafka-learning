package org.learning.kafka.common;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;

public enum KafkaProperties {

    CONSUMER() {

        @Override
        public void setProperties(Properties properties) {
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "java-topic-consumer-group");
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        }

    },

    PRODUCER() {

        @Override
        public void setProperties(Properties properties) {
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
            properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");

            properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        }

    };

    private final Properties properties;

    KafkaProperties() {
        properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        setProperties(properties);
    }

    public abstract void setProperties(Properties properties);

    public Properties getProperties() {
        return properties;
    }

    public Properties overrideProperties(Map<String, String> newProperties) {
        newProperties.forEach(properties::setProperty);
        return properties;
    }

}
