package org.learning.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.learning.kafka.common.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoWithShutdown {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoWithShutdown.class.getSimpleName());

    private static final String TOPIC = "java_topic";

    public static void main(String[] args) {
        Properties properties = KafkaProperties.CONSUMER.getProperties();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            addConsumerShutdownHook(consumer);

            consumer.subscribe(Collections.singleton(TOPIC));

            while (true) {
                log.info("Polling");

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: {}, Value: {}", record.key(), record.value());
                    log.info("Partition: {}, Offset: {}", record.partition(), record.offset());
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer is starting to shut down");
        } catch (Exception e) {
            log.error("Unexpected exception in the consumer", e);
        }
    }

    private static void addConsumerShutdownHook(KafkaConsumer<String, String> consumer) {
        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown");
            consumer.wakeup();

            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

}
