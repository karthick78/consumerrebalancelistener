package com.example.rebalancelistener;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TestConsumerRebalanceListener implements ConsumerRebalanceListener {

    final List<Future<Boolean>> futures = new ArrayList<>();
    private final KafkaConsumer<String, String> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private String name=null;

    public TestConsumerRebalanceListener(final KafkaConsumer<String, String> consumer, String name) {
        this.consumer = consumer;
        this.name=name;
    }

    @Override
    public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {
        System.out.println(" Called onPartitionsRevoked with partitions: " + partitions);
        if(!futures.isEmpty())
            futures.get(0).cancel(true);
        System.out.printf("onPartitionsRevoked - consumerName: %s, partitions: %s%n",name,
                formatPartitions(partitions));

        //consumer.commitSync(currentOffsets);
        currentOffsets.clear();
    }

    public void addOffset(final String topic, final int partition, final long offset) {
        currentOffsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset));

    }

    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        System.out.println("Called onPartitionsAssigned with partitions: " + partitions);
        System.out.printf("onPartitionsAssigned - consumerName: %s, partitions: %s%n",name,
                formatPartitions(partitions));
    }

    private static List<String> formatPartitions(Collection<TopicPartition> partitions) {
        return partitions.stream().map(topicPartition ->
                String.format("topic: %s, partition: %s", topicPartition.topic(), topicPartition.partition()))
                .collect(Collectors.toList());
    }

}