package com.tracesafe.cache.updator.Configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisPubSubConf {

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
	
	@Bean(name = "jediscacheconnection")
	JedisConnectionFactory jedisCacheConnectionFactory() {
		JedisConnectionFactory jedisfactory = new JedisConnectionFactory();
		jedisfactory.setDatabase(549);
		jedisfactory.setHostName(redishost);
		jedisfactory.setPort(redisport);
		return jedisfactory;
	}

	@Bean(name = "cacheConnectionTemplate")
	StringRedisTemplate cacheConnectionTemplate() {
		StringRedisTemplate template = new StringRedisTemplate(jedisCacheConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer(Object.class));
        template.setValueSerializer(new GenericToStringSerializer(Object.class));
		return template;
	}

}