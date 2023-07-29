package org.learning.kafka.opensearch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.learning.kafka.common.KafkaProperties;
import org.opensearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.learning.kafka.opensearch.RestHighLevelClientFacade.*;

public class OpenSearchConsumer {

    private static final Logger log = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

    private static final String INDEX = "wikimedia";

    private static final String TOPIC = "wikimedia.recentchange";

    public static void main(String[] args) throws IOException {
        RestHighLevelClient openSearchClient = createRestHighLevelClient();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaProperties.CONSUMER.overrideProperties(
                Map.of(ConsumerConfig.GROUP_ID_CONFIG, String.format("%s-consumer-group", TOPIC),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")));

        try (openSearchClient; consumer) {
            if (!indexExists(openSearchClient, INDEX)) {
                createIndex(openSearchClient, INDEX);
            }

            consumer.subscribe(Collections.singleton(TOPIC));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));

                log.info("Received {} records", records.count());

                for (ConsumerRecord<String, String> record : records) {
                    request(openSearchClient, INDEX, record.value());
                }
            }
        }
    }

}
