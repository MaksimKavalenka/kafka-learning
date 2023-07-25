package org.learning.kafka.wikimedia;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.learning.kafka.common.KafkaProperties;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class WikimediaChangesProducer {

    private static final String TOPIC = "wikimedia.recentchange";

    private static final String URL = "https://stream.wikimedia.org/v2/stream/recentchange";

    public static void main(String[] args) throws InterruptedException {
        KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaProperties.PRODUCER.getProperties());
        BackgroundEventHandler eventHandler = new WikimediaChangeHandler(producer, TOPIC);

        try (BackgroundEventSource eventSource = buildBackgroundEventSource(eventHandler)) {
            eventSource.start();

            TimeUnit.SECONDS.sleep(10);
        }
    }

    private static BackgroundEventSource buildBackgroundEventSource(BackgroundEventHandler eventHandler) {
        EventSource.Builder eventSourceBuilder = new EventSource.Builder(URI.create(URL));
        return new BackgroundEventSource.Builder(eventHandler, eventSourceBuilder).build();
    }

}
