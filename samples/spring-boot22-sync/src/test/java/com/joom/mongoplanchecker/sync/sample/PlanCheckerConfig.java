package com.joom.mongoplanchecker.sync.sample;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.sync.data.PlanCheckerMongoDbFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

@Configuration
public class PlanCheckerConfig {

  @Bean
  public PlanChecker planChecker() {
    return new PlanChecker();
  }

  @Bean
  public BeanPostProcessor mongoDbFactoryPostProcessor() {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName)
          throws BeansException {
        if (bean instanceof MongoDbFactory) {
          return new PlanCheckerMongoDbFactory((MongoDbFactory) bean, planChecker());
        }
        return bean;
      }
    };
  }
}
