package org.learning.kafka.streams;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.learning.kafka.common.KafkaProperties;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BankTransactionsProducer {

    public static void main(String[] args) {
        KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaProperties.PRODUCER.overrideProperties(
                Map.of(ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.RETRIES_CONFIG, "3",
                        ProducerConfig.LINGER_MS_CONFIG, "1",
                        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")));

        try (producer) {
            while (true) {
                producer.send(newRandomTransaction("client1"));
                Thread.sleep(100);
                producer.send(newRandomTransaction("client2"));
                Thread.sleep(100);
                producer.send(newRandomTransaction("client3"));
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static ProducerRecord<String, String> newRandomTransaction(String name) {
        ObjectNode transaction = JsonNodeFactory.instance.objectNode();
        Integer amount = ThreadLocalRandom.current().nextInt(0, 100);
        Instant now = Instant.now();
        transaction.put("name", name);
        transaction.put("amount", amount);
        transaction.put("time", now.toString());
        return new ProducerRecord<>("bank-transactions", name, transaction.toString());
    }

}
