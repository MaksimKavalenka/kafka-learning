package org.learning.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.learning.kafka.common.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class.getSimpleName());

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = KafkaProperties.PRODUCER.getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            IntStream.range(1, 10)
                    .forEach(counter -> produceMessage(producer, counter));
        }
    }

    private static void produceMessage(KafkaProducer<String, String> producer, int counter) {
        String key = String.format("key_#%d", counter);

        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(TOPIC, key, String.format("key message #%d", counter));

        producer.send(producerRecord, (metadata, exception) -> {
            if (Objects.isNull(exception)) {
                log.info("Key: {} | Partition: {}", key, metadata.partition());
            } else {
                log.error("Error while producing", exception);
            }
        });
    }

}
