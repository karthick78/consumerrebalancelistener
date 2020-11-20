package com.example.rebalancelistener;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

public class RandomProducer {

    public static void main(String args[]){

        String topicName="test_topic";
        String msg="";

        Properties props=new Properties();
       // props.put("bootstrap.servers","0.0.0.0:19092,0.0.0.0:29092");
        props.put("bootstrap.servers","127.0.0.1:9092,127.0.0.1:9091,127.0.0.1:9093");

        //props.put("bootstrap.servers","kafka-1:19092,kafka-2:29092,kafka-3:39092");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer <String,String> producer=new KafkaProducer(props);

        Random rg= new Random();

        Calendar dt= Calendar.getInstance();
        dt.set(2020,11,16);
        try{
            while(true)
            {
                for(int i=0;i<100;i++){
                    msg=dt.get(Calendar.YEAR) +"##" + dt.get(Calendar.MONTH) +"##" + dt.get(Calendar.DATE);
                    producer.send(new ProducerRecord<String, String>(topicName, 0, "key", msg)).get();
                    msg=dt.get(Calendar.YEAR) +"##" + dt.get(Calendar.MONTH) +"##" + dt.get(Calendar.DATE);
                    producer.send(new ProducerRecord<String, String>(topicName, 1, "key", msg)).get();
                    msg=dt.get(Calendar.YEAR) +"##" + dt.get(Calendar.MONTH) +"##" + dt.get(Calendar.DATE);
                    producer.send(new ProducerRecord<String, String>(topicName, 2, "key", msg)).get();
                }
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        finally {
            producer.close();
        }



    }


}
