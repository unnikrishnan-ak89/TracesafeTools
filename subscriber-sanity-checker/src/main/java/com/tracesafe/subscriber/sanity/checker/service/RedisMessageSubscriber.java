package com.tracesafe.subscriber.sanity.checker.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import com.tracesafe.subscriber.sanity.checker.utils.BeanUtil;
import com.tracesafe.subscriber.sanity.checker.utils.RedisUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisMessageSubscriber implements MessageListener {
	
	@Override
	public void onMessage(Message message, byte[] pattern) {

		String msg = new String(message.getBody());
		String[] arr = msg.split(":");
		
		LOGGER.info("RedisPublish recieved with command : \"publish {} {}\"", RedisUtils.SANITY_CHECKER_TOPIC, msg);
		SanityCheckerService sanityCheckerService = BeanUtil.getBean(SanityCheckerService.class);
		
		switch (arr[0]) {
		case RedisUtils.CMD_INITIATE_SANITY_CHECK:
			sanityCheckerService.initiateTest(arr[1]);
			break;
		default:
			LOGGER.warn("Invalid command : {} recieved in \"{}\" topic. Nothing to process", arr[0], RedisUtils.SANITY_CHECKER_TOPIC);
		}
	}

}
