package com.wan.kafkastreaming;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.util.Properties;

/**
 * @author wanfeng
 * @date 2020/1/31 21:35 用于连接Kafka与flume之间的管道
 */
public class Application {
  public static void main(String[] args) {

    String brokers = "wan:9092"; // Kafka端口
    String zookeepers = "wan:2181"; // Zookeeper端口

    // 定义输入和输出的topic
    String from = "log";
    String to = "recommender";

    Properties settings = new Properties();
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeepers);
    settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MyEventTimeExtractor.class);

    StreamsConfig config = new StreamsConfig(settings);

    TopologyBuilder builder = new TopologyBuilder();

    builder
        .addSource("SOURCE", from)
        .addProcessor("PROCESSOR", () -> new LogProcessor(), "SOURCE")
        .addSink("SINK", to, "PROCESSOR");

    KafkaStreams streams = new KafkaStreams(builder, config);
    streams.start();
    System.out.println("kafka stream started!");
  }
}
