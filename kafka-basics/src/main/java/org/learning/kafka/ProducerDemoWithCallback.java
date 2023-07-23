package org.learning.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {
        Properties properties = getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            IntStream.range(1, 4)
                    .forEach(iteration -> {
                        IntStream.range(1, 10)
                                .forEach(counter -> produceMessage(producer, iteration, counter));

                        producer.flush();
                    });
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        return  properties;
    }

    private static void produceMessage(KafkaProducer<String, String> producer, int iteration, int counter) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>("java_topic", String.format("callback message #%d.%d", iteration, counter));

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
