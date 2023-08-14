package org.learning.kafka.opensearch;

import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.learning.kafka.common.KafkaProperties;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static org.learning.kafka.opensearch.RestHighLevelClientFacade.*;

public class OpenSearchConsumer {

    private static final Logger log = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

    private static final String INDEX = "wikimedia";

    private static final String TOPIC = "wikimedia.recentchange";

    public static void main(String[] args) throws IOException {
        RestHighLevelClient openSearchClient = createRestHighLevelClient();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaProperties.CONSUMER.overrideProperties(
                Map.of(ConsumerConfig.GROUP_ID_CONFIG, String.format("%s-consumer-group", TOPIC),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest",
                        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")));

        try (openSearchClient; consumer) {
            addConsumerShutdownHook(consumer, openSearchClient);

            if (!indexExists(openSearchClient, INDEX)) {
                createIndex(openSearchClient, INDEX);
            }

            consumer.subscribe(Collections.singleton(TOPIC));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));

                log.info("Received {} records", records.count());

                List<IndexRequest> requests = new LinkedList<>();

                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    requests.add(createRequest(INDEX, getId(value), value));
                }

                if (!requests.isEmpty()) {
                    sendBulkRequest(openSearchClient, requests);

                    consumer.commitSync();
                    log.info("Offsets have been committed");
                }
            }
        }
    }

    private static String getId(String value) {
        return JsonParser.parseString(value)
                .getAsJsonObject()
                .getAsJsonObject("meta")
                .getAsJsonPrimitive("id")
                .getAsString();

    }

    private static void addConsumerShutdownHook(KafkaConsumer<String, String> consumer, AutoCloseable... closeables) {
        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown");
            consumer.wakeup();

            try (consumer) {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                for (AutoCloseable closeable : closeables) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

}
