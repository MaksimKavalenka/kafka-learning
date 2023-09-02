package org.learning.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.learning.kafka.common.KafkaStreamsProperties;

public class FavoriteColor {

    private static final String INPUT_TOPIC_NAME = "favorite-color-input";

    private static final String OUTPUT_TOPIC_NAME = "favorite-color-output";

    private static final String USER_FAVORITE_COLOR_TOPIC_NAME = "user-favorite-color";

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = createFavoriteColorTopology(builder);

        KafkaStreams streams = new KafkaStreams(topology, KafkaStreamsProperties.FAVORITE_COLOR_STREAM.getProperties());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Topology createFavoriteColorTopology(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(INPUT_TOPIC_NAME);

        KStream<String, String> userFavoriteColor = stream
                .filter((key, value) -> value.contains(","))
                .map((key, value) -> {
                    String[] values = value.split(",");
                    return KeyValue.pair(values[0].toLowerCase(), values[1].toLowerCase());
                });

        userFavoriteColor.to(USER_FAVORITE_COLOR_TOPIC_NAME);

        KTable<String, String> table = builder.table(USER_FAVORITE_COLOR_TOPIC_NAME);

        KTable<String, Long> favoriteColor = table
                .groupBy((user, color) -> KeyValue.pair(color, color))
                .count(Named.as("counts"));

        favoriteColor.toStream().to(OUTPUT_TOPIC_NAME, Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

}
