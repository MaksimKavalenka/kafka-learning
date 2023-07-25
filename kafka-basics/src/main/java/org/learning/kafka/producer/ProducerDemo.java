package org.learning.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.learning.kafka.common.KafkaProperties;

import java.util.Properties;

public class ProducerDemo {

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = KafkaProperties.PRODUCER.getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, "hello world");

            producer.send(producerRecord);
            producer.flush();
        }
    }

}
