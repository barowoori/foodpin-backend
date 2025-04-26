package com.barowoori.foodpinbackend.config;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationEventConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public InitializingBean eventsInitializer(ApplicationEventPublisher applicationEventPublisher){
        return ()-> NotificationEvent.setPublisher(applicationEventPublisher);
    }
}
