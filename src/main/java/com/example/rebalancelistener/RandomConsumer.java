package com.example.rebalancelistener;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

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
        String groupName="RG100";
        Properties props=new Properties();
        //props.put("bootstrap.servers","localhost:19092,localhost:29092");
       // props.put("bootstrap.servers","127.0.0.1:9092,127.0.0.1:9091,127.0.0.1:9093");
        //props.put("bootstrap.servers","localhost:9092,localhost:9091,localhost:9093");
        props.put("bootstrap.servers","0.0.0.0:9092,0.0.0.0:9091,0.0.0.0:9093");
        //props.put("bootstrap.servers","192.168.99.100:9092,192.168.99.100:9093192.168.99.100:9094");
        props.put("group.id",groupName);
       // props.put();
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset","earliest");
        props.put("enable.auto.commit", "false");
        ConsumerRebalanceListener objConsumerRebalanceListener=null;
        KafkaConsumer<String,String> consumer=new KafkaConsumer(props);
        final int minBatchSize = 200;


        //TestConsumerRebalanceListener objTestConsumerRebalanceListener= new TestConsumerRebalanceListener(consumer,name);
        consumer.subscribe(Collections.singleton(topicName));
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        try{
           while(true){
                ConsumerRecords<String,String> records=consumer.poll(1000);
            System.out.println("record##1234#"+consumer.listTopics());
            System.out.println("record##1234#"+records.count());

                for(ConsumerRecord<String,String> record: records){
                    System.out.println("record##1234#"+record.topic());
                    System.out.println("record##1234#"+record.partition());
                    System.out.println("record##1234#value###"+record.value());
                    buffer.add(record);
                    //objTestConsumerRebalanceListener.addOffset(record.topic(),record.partition(), record.offset());

                }
               if (buffer.size() >= minBatchSize) {
                   //insertIntoDb(buffer);
                   consumer.commitSync();
                   System.out.println("consumer commit###");
                   buffer.clear();
                   System.out.println("buffer clear###");
               }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

          props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:9092,127.0.0.1:9091,127.0.0.1:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put("auto.offset.reset","earliest");
        props.put("enable.auto.commit", "false");
        // Create the consumer using props.
        //final Consumer<Long, String> consumer =       new KafkaConsumer<>(props);

         /*props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092,127.0.0.1:9091,127.0.0.1:9093");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");*/
         consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test_topic"));
       // final int minBatchSize = 200;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            System.out.println("record234###"+consumer.listTopics());
            System.out.println("record234###"+records.count());

            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
                System.out.println("record234###"+record);
            }
            if (buffer.size() >= minBatchSize) {
                //insertIntoDb(buffer);
                consumer.commitSync();
                buffer.clear();
            }
        }

        //consumer.poll(Duration.ofSeconds(10));
        //System.out.printf("closing consumerName: %s%n", name);
        //consumer.close();


    }


}


