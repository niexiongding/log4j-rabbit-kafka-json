package com.kafka.java;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 *
 * @author Nie Xiongding
 * @since 2015年11月6日 下午8:18:37
 */
public class ProducerDemo {

    private Producer<String, String> producer = null;
    public static final String TOPIC = "topic1";

    private ProducerDemo() {
        if (null == this.producer) {
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            //control the criteria under which requests are considered complete."all" :slowest but most durable
            props.put("acks", "all");
            //requests failed automatically retry
            props.put("retries", 0);
            //unsent records buffer
            props.put("batch.size", 16384);
            //requests sent interval
            props.put("linger.ms", 1);
            //producer buffer size
            props.put("buffer.memory", 33554432);
            //turn key into bytes
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            //turn value into bytes
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            
            producer = new KafkaProducer<String, String>(props);
        }
    }
    
    public void sendBlock() {
        String key = String.valueOf("test-for-blocking-key");
        String data = "test-for-blocking-value";
        try {
            RecordMetadata r = producer.send(new ProducerRecord<String, String>(TOPIC, key, data)).get();
            System.out.println("block send return: " + r.toString());
            producer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendNonBlock() {
        String key = String.valueOf("test-for-non-blocking-key");
        String data = "test-for-non-blocking-value";
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, key, data);
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    System.out.println("The offset of the record we just sent is: " + metadata.offset());
                }
            }
        });
    }

    public static void main(String[] args) {
        ProducerDemo demo = new ProducerDemo();
        demo.sendBlock();
//        demo.sendNonBlock();
    }
}
