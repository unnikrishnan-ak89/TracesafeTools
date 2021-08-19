/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.cache.updator.services;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tracesafe.cache.updator.BeanUtil;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Service
@Slf4j
@NoArgsConstructor
public class CacheUpdateTask extends TimerTask {
	
	private static StringRedisTemplate cacheConnectionTemplate;
	private static boolean valueCheckEnabled;
	private static String keyPattern;
	
	public CacheUpdateTask(Boolean valueCheckenabled, String pattern) {
		cacheConnectionTemplate = BeanUtil.getBean("cacheConnectionTemplate", StringRedisTemplate.class);
		valueCheckEnabled = valueCheckenabled;
		keyPattern = pattern;
	}
	
	@Override
	public synchronized void run() {	
		try (RedisConnection connection = cacheConnectionTemplate.getConnectionFactory().getConnection()) {
            Set<byte[]> keys = (Set<byte[]>)connection.keys(keyPattern.getBytes());
            if (null == keys) {
                LOGGER.debug("no keys found with pattern \"{}\" from redis", keyPattern);
                return;
            }
            LOGGER.info("Total number of objects : {} with pattern \"{}\" ", keys.size(), keyPattern);
            Set<String> delKeySet = new HashSet<String>();
            for (byte[] data : keys) {
                String key = new String(data, 0, data.length);
                if (!valueCheckEnabled) {
                	delKeySet.add(key);
                	continue;
                }
                String value = cacheConnectionTemplate.opsForValue().get(key);
                if (null == value || value.trim().equalsIgnoreCase("null")) {
                	LOGGER.info("Value against key : {} found null.. Deleting entry", key);
                	delKeySet.add(key);
                }
            }

            LOGGER.info("Total number of objects for deletion : {}", delKeySet.size());
            if (delKeySet.size() > 0) {
            	 Long delete = cacheConnectionTemplate.delete(delKeySet);
                 LOGGER.info("{} keys deleted from redis cache....", delete);
                 
                 keys = (Set<byte[]>)connection.keys(keyPattern.getBytes());
                 LOGGER.info("Total number of objects after deletion : {}", keys.size());
            }
            LOGGER.info("***************************************************");
        } catch (Exception e) {
            LOGGER.error("Exception on cache delete", e);
        }
	}
}
