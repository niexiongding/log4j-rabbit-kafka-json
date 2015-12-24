package com.kafka.scala;

/**
 *
 * @author Nie Xiongding
 * @since 2015年12月24日 上午10:58:19
 */
public interface KafkaProperties {
    final static String zkConnect = "localhost:2181";
    final static String groupId = "group1";
    final static String topic = "topic1";
    final static String kafkaServerURL = "10.22.10.139";
    final static int kafkaServerPort = 9092;
    final static int kafkaProducerBufferSize = 64 * 1024;
    final static int connectionTimeOut = 20000;
    final static int reconnectInterval = 10000;
    final static String topic2 = "topic2";
    final static String topic3 = "topic3";
    final static String clientId = "SimpleConsumerDemoClient";
}
