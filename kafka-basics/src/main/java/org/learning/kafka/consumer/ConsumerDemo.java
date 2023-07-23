package org.learning.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.learning.kafka.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = KafkaProperties.CONSUMER.getProperties();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(TOPIC));

            while (true) {
                log.info("Polling");

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info(String.format("Key: %s, Value: %s", record.key(), record.value()));
                    log.info(String.format("Partition: %d, Offset: %d", record.partition(), record.offset()));
                }
            }
        }
    }

}
