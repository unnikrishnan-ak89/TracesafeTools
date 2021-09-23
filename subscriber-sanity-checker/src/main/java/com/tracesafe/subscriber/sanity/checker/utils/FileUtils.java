/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker.utils;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Slf4j
public class FileUtils {

	public static InputStream loadFileStream(String resourceLocation) {
		LOGGER.info("Running loadFile() with resourceLocation : {}", resourceLocation);
		InputStream inputStream = null;
		try {
			Resource resource = new FileSystemResource(resourceLocation);
			if(!resource.exists()) {
				return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceLocation);
			}
			inputStream = resource.getInputStream();
		} catch (IOException e) {
			LOGGER.error("IOException on loadFileStream with resourceLocation : {}", resourceLocation, e);
		}
		return inputStream;
	}
	
}
