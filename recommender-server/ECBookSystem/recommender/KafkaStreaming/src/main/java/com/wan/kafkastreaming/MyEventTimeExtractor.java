package com.wan.kafkastreaming;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * @author wanfeng
 * @date 2020/2/2 21:23
 */
public class MyEventTimeExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {
        return 0;
    }
}
