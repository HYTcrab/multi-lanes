package com.wangyy.multilanes.core.kafka;

import com.wangyy.multilanes.core.annotation.ConditionalOnConfig;
import com.wangyy.multilanes.core.kafka.consumer.KafkaConsumerAspect;
import com.wangyy.multilanes.core.kafka.consumer.KafkaConsumerTopicRegisterProcessor;
import com.wangyy.multilanes.core.kafka.producer.KafkaProducerAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by houyantao on 2022/12/28
 */
@ConditionalOnConfig("multi-lanes.enable")
@Configuration
@Slf4j
public class KafkaMultiLanesContextInit {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public KafkaNodeWatcher kafkaNodeWatcher(CuratorFramework curatorFramework) {
        return new KafkaNodeWatcher(curatorFramework);
    }

    @Bean
    public KafkaProducerAspect kafkaProducerAspect() {
        return new KafkaProducerAspect();
    }

    @Bean
    public KafkaConsumerAspect kafkaConsumerAspect(KafkaNodeWatcher kafkaNodeWatcher) {
        return new KafkaConsumerAspect(kafkaNodeWatcher);
    }

    @PostConstruct
    public void init() {
        KafkaNodeWatcher kafkaNodeWatcher = applicationContext.getBean(KafkaNodeWatcher.class);
        new KafkaConsumerTopicRegisterProcessor(applicationContext, kafkaNodeWatcher).registerToZookeeper();
    }
}
