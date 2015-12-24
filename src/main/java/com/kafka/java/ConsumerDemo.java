package com.kafka.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 *
 * @author Nie Xiongding
 * @since 2015年11月9日 下午8:57:13
 */
public class ConsumerDemo {
    private Consumer<String, String> consumer = null;
    public static final String TOPIC = "test";

    private ConsumerDemo() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("partition.assignment.strategy", "roundrobin");
        consumer = new KafkaConsumer<String, String>(props);
    }

    /**
     * 
     * @param records
     * @return the offset of the latest processed message per partition
     */
    private Map<TopicPartition, Long> process(Map<String, ConsumerRecords<String, String>> records) {
        Map<TopicPartition, Long> processedOffsets = new HashMap<TopicPartition, Long>();
        for (Entry<String, ConsumerRecords<String, String>> recordMetadata : records.entrySet()) {
            List<ConsumerRecord<String, String>> recordsPerTopic = recordMetadata.getValue().records();
            for (int i = 0; i < recordsPerTopic.size(); i++) {
                ConsumerRecord<String, String> record = recordsPerTopic.get(i);
                // process record
                try {
                    System.out.println("topic = " + record.topic() + ", key =  " + record.key() + " , value = " + record.value());
                    processedOffsets.put(record.topicAndPartition(), record.offset());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return processedOffsets;
    }

    public void consume() {
        consumer.subscribe(TOPIC);
        Map<TopicPartition, Long> offsets = new HashMap<TopicPartition, Long>();
        offsets.put(new TopicPartition(TOPIC, 0), 0l);
        consumer.seek(offsets);
        boolean isRunning = true;
        while (isRunning) {
            Map<String, ConsumerRecords<String, String>> records = consumer.poll(100);
            process(records);
        }
        consumer.close();
    }

    public static void main(String[] args) {
        ConsumerDemo consumerDemo = new ConsumerDemo();
        consumerDemo.consume();
    }
}
