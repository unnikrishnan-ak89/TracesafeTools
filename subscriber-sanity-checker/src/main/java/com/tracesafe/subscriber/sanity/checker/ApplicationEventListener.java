/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Slf4j
@Configuration
public class ApplicationEventListener {
	
	@Autowired
	private Environment environment;
	
	@EventListener(ApplicationReadyEvent.class)
	public void appInit() {
		LOGGER.info("*********************************************");
		LOGGER.info("Subscriber-sanity-checker initiated....");
		LOGGER.info("*********************************************");
		
	}
}
