package com.tracesafe.subscriber.sanity.checker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.tracesafe.subscriber.sanity.checker.service.RedisMessageSubscriber;
import com.tracesafe.subscriber.sanity.checker.utils.RedisUtils;

@Configuration
public class RedisConfig {

	@Value("${redis.url}")
	String redishost;

	@Value("${redis.port}")
	int redisport;

	@Bean
	@Primary
	JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "bridgeInfoConnection")
	JedisConnectionFactory bridgeInfoConnection() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(RedisUtils.DB_BRIDGE_INFO);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "bridgeinfotemplate")
	StringRedisTemplate jedisbridgeinfotemplate() {
		StringRedisTemplate template = new StringRedisTemplate(bridgeInfoConnection());
		return template;
	}

	@Bean(name = "tagInfoConnection")
	JedisConnectionFactory tagInfoConnection() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(RedisUtils.DB_TAG_INFO);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "taginfotemplate")
	StringRedisTemplate jedistaginfotemplate() {
		StringRedisTemplate template = new StringRedisTemplate(tagInfoConnection());
		return template;
	}

	@Bean(name = "tagCheckinTimeConnection")
	JedisConnectionFactory tagCheckinTimeConnection() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(RedisUtils.DB_TAG_CHECKIN_TIME);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "tagCheckinTimeTemplate")
	StringRedisTemplate tagCheckinTimeTemplate() {
		return new StringRedisTemplate(tagCheckinTimeConnection());
	}

	@Bean(name = "siteTimezoneConnectionFactory")
	JedisConnectionFactory siteTimezoneConnectionFactory() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(RedisUtils.DB_SUBORG_TIMEZONE);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "subOrgTimezoneTemplate")
	StringRedisTemplate jedisSubOrgTimezoneDbConnection() {
		return new StringRedisTemplate(siteTimezoneConnectionFactory());
	}

	@Bean
	MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new RedisMessageSubscriber());
	}

	@Bean
	RedisMessageListenerContainer redisContainer() {
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory());
		container.addMessageListener(messageListener(), topic());
		return container;
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic(RedisUtils.SANITY_CHECKER_TOPIC);
	}

	@Bean(name = "cacheConnection")
	JedisConnectionFactory cacheConnection() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(RedisUtils.DB_GENERAL_CACHE);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean
	CacheManager cacheManager() {
		return RedisCacheManager.builder(cacheConnection()).build();
	}

}