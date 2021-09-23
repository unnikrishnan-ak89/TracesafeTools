/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.tracesafe.subscriber.sanity.checker.pojo.TagProximityPacket;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Slf4j
public class CommonUtils {

	public static TagProximityPacket getTagProximityPacketData(InputStream csvDataStream) {
		InputStreamReader streamReader = new InputStreamReader(csvDataStream, StandardCharsets.UTF_8);

		boolean headerLine = true;
		try (BufferedReader reader = new BufferedReader(streamReader)) {
			for (String line; (line = reader.readLine()) != null;) {
				if (headerLine) {
					headerLine = false;
					continue; // skipping file header
				}
				try {
					if (StringUtils.isBlank(line)) {
						return null;
					}
					String[] arr = line.split(",");
					int sequenceNumber = StringUtils.isNotBlank(arr[0]) ? Integer.parseInt(arr[0].trim()) : 0;
					int protocolVersion = StringUtils.isNotBlank(arr[1]) ? Integer.parseInt(arr[1].trim()) : 0;
					long syncTime = StringUtils.isNotBlank(arr[2]) ? Long.parseLong(arr[2].trim()) : 0;
					long beaconLoggerTick = StringUtils.isNotBlank(arr[3]) ? Long.parseLong(arr[3].trim()) : 0;
					long extractTime = StringUtils.isNotBlank(arr[4]) ? Long.parseLong(arr[4].trim()) : 0;
					int reserved = StringUtils.isNotBlank(arr[5]) ? Integer.parseInt(arr[5].trim()) : 0;
					int battery = StringUtils.isNotBlank(arr[6]) ? Integer.parseInt(arr[6].trim()) : 1;
					int count = StringUtils.isNotBlank(arr[7]) ? Integer.parseInt(arr[7].trim()) : 0;
					int rssi = StringUtils.isNotBlank(arr[8]) ? Integer.parseInt(arr[8].trim()) : 0;
					return new TagProximityPacket(sequenceNumber, protocolVersion, syncTime, beaconLoggerTick, extractTime, reserved, battery, count, rssi);
				} catch (NumberFormatException e) {
					LOGGER.error("Exception while mapping csv data into csvData Object. Error : {}, Line : {}", e.getMessage(), line);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception while reading TagProximityPacket from csvFile", e);
		}
		return null;
	}
}
