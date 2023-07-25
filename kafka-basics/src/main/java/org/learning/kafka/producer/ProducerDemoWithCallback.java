package org.learning.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.learning.kafka.common.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = KafkaProperties.PRODUCER.getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            IntStream.range(1, 4)
                    .forEach(iteration -> {
                        IntStream.range(1, 10)
                                .forEach(counter -> produceMessage(producer, iteration, counter));
                    });
        }
    }

    private static void produceMessage(KafkaProducer<String, String> producer, int iteration, int counter) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(TOPIC, String.format("callback message #%d.%d", iteration, counter));

        producer.send(producerRecord, (metadata, exception) -> {
            if (Objects.isNull(exception)) {
                log.info(String.format("Received new metadata \nTopic: %s\nPartition: %d\nOffset: %d\nTimestamp: %d\n",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp()));
            } else {
                log.error("Error while producing", exception);
            }
        });
    }

}
