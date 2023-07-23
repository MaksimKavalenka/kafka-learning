package org.learning.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class.getSimpleName());

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            IntStream.range(1, 10)
                    .forEach(counter -> produceMessage(producer, counter));
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        return  properties;
    }

    private static void produceMessage(KafkaProducer<String, String> producer, int counter) {
        String key = String.format("key_#%d", counter);

        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(TOPIC, key, String.format("key message #%d", counter));

        producer.send(producerRecord, (metadata, exception) -> {
            if (Objects.isNull(exception)) {
                log.info(String.format("Key: %s | Partition: %d", key, metadata.partition()));
            } else {
                log.error("Error while producing", exception);
            }
        });
    }

}
