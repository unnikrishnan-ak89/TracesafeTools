package com.tracesafe.subscriber.sanity.checker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableIntegration
@EnableAspectJAutoProxy
@EnableScheduling
public class SubscriberSanityCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriberSanityCheckerApplication.class, args);
	}

}
