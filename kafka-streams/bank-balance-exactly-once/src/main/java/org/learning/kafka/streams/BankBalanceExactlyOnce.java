package org.learning.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.learning.kafka.common.KafkaStreamsProperties;

import java.time.Instant;
import java.util.Map;

public class BankBalanceExactlyOnce {

    private static final String INPUT_TOPIC_NAME = "bank-transactions";

    private static final String OUTPUT_TOPIC_NAME = "bank-balance";

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = createBankBalanceTopology(builder);

        KafkaStreams streams = new KafkaStreams(topology, KafkaStreamsProperties.BANK_BALANCE.overrideProperties(
                Map.of(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2)));

        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Topology createBankBalanceTopology(StreamsBuilder builder) {
        Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        Serde<JsonNode> serde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStream<String, JsonNode> stream = builder.stream(INPUT_TOPIC_NAME, Consumed.with(Serdes.String(), serde));

        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0).toString());

        KTable<String, JsonNode> clientBankBalance = stream
                .groupByKey()
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as("bank-balance-agg")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(serde));

        clientBankBalance.toStream().to(OUTPUT_TOPIC_NAME, Produced.with(Serdes.String(), serde));

        return builder.build();
    }

    private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
        long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
        long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));

        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());
        newBalance.put("time", newBalanceInstant.toString());

        return newBalance;
    }

}
