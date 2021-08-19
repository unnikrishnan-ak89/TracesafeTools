/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.cache.updator;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.tracesafe.cache.updator.services.CacheUpdateTask;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Slf4j
@Configuration
public class ApplicationEventListener {
	
	@Value("${cache.update.interval:1}")
	private long interval;
	
	
	@Value("${cache.value.null.check.enabled:false}")
	private boolean valueCheckEnabled;
	
	@Value("${cache.clear.key.pattern:*}")
	private String keyPattern;
	
	@Autowired
	@Qualifier("cacheConnectionTemplate")
	private StringRedisTemplate cacheConnectionTemplate;
	
	@Autowired
	private Environment environment;
	
	@EventListener(ApplicationReadyEvent.class)
	public void appInit() {
		LOGGER.info("*********************************************");
		LOGGER.info("SubscriberCacheUpdator Application initiated....");
		LOGGER.info("redis.url : {}", environment.getProperty("redis.url"));
		LOGGER.info("redis.port : {}", environment.getProperty("redis.port"));
		LOGGER.info("cache.update.interval : {} minutes", environment.getProperty("cache.update.interval"));
		LOGGER.info("*********************************************");
		
		LOGGER.info("Scheduling CacheUpdateTask with {} minutes interval", interval);
		new Timer().schedule(new CacheUpdateTask(valueCheckEnabled, keyPattern), 0, interval * 60000);	
	}
}
