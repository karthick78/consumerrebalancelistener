package com.example.rebalancelistener;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RandomConsumer {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorService.execute(() -> startConsumer("consumer-" + finalI));
            Thread.sleep(3000);
        }
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.MINUTES);
    }


    private static KafkaConsumer<String, String> startConsumer(String name){

        String topicName="test_topic";
        String msg="";
        String groupName="RG";
        Properties props=new Properties();
        //props.put("bootstrap.servers","localhost:19092,localhost:29092");
        props.put("bootstrap.servers","127.0.0.1:9092,127.0.0.1:9091,127.0.0.1:9093");
        props.put("group.id",groupName);
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        ConsumerRebalanceListener objConsumerRebalanceListener=null;
        KafkaConsumer<String,String> consumer=new KafkaConsumer(props);


        TestConsumerRebalanceListener objTestConsumerRebalanceListener= new TestConsumerRebalanceListener(consumer,name);
        consumer.subscribe(Collections.singleton(topicName),objTestConsumerRebalanceListener);
        try{
            //while(true){
                ConsumerRecords<String,String> records=consumer.poll(100);
                for(ConsumerRecord<String,String> record: records){
                    System.out.println("record###"+record.topic());
                    System.out.println("record###"+record.partition());
                    System.out.println("record###"+record.value());
                    objTestConsumerRebalanceListener.addOffset(record.topic(),record.partition(), record.offset());

                }
            //}
        }catch(Exception e){
            e.printStackTrace();
        }

        consumer.poll(Duration.ofSeconds(10));
        //System.out.printf("closing consumerName: %s%n", name);
        consumer.close();



    return consumer;

    }


}


