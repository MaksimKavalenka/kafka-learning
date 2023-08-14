package org.learning.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.learning.kafka.common.KafkaProperties;

import java.util.Arrays;

public class WordCount {

    private static final String INPUT_TOPIC_NAME = "word-count-input";

    private static final String OUTPUT_TOPIC_NAME = "word-count-output";

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        Topology topology = builder.build();

        KafkaStreams streams = new KafkaStreams(topology, KafkaProperties.WORD_COUNT_STREAM.getProperties());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static void createWordCountStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(INPUT_TOPIC_NAME);

        KTable<String, Long> wordCount = stream
                .mapValues(value -> value.toLowerCase())
                .flatMapValues(value -> Arrays.asList(value.split(" ")))
                .selectKey((key, value) -> value)
                .groupByKey()
                .count(Named.as("counts"));

        wordCount.toStream().to(OUTPUT_TOPIC_NAME, Produced.with(Serdes.String(), Serdes.Long()));
    }

}
