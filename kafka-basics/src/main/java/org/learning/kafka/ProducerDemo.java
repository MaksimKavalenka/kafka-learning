package org.learning.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        Properties properties = getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("java_topic", "hello world");

            producer.send(producerRecord);
            producer.flush();
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", "http://localhost:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        return  properties;
    }

}
