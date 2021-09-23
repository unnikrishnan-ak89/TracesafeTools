/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
@Getter
@Setter
public class TagProximityPacket {
	
	private static final String PIPE = "|";

	private int sequenceNumber;
	private int protocolVersion;
	private long syncTime;
	private long beaconLoggerTick;
	private long extractTime;
	
	private int reserved;
	private int battery; //  {100, 85, 71, 57, 42, 28, 14, 0 };
	private int count;
	private int rssi;
	
	private long lastseen;
	
	public TagProximityPacket() {}

	public TagProximityPacket(int sequenceNumber, int protocolVersion, long syncTime, long beaconLoggerTick,
			long extractTime, int reserved, int battery, int count, int rssi) {
		
		super();
		this.sequenceNumber = sequenceNumber;
		this.protocolVersion = protocolVersion;
		this.syncTime = syncTime;
		this.beaconLoggerTick = beaconLoggerTick;
		this.extractTime = extractTime;
		this.reserved = reserved;
		this.battery = battery;
		this.count = count;
		this.rssi = rssi;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[")
		.append(PIPE).append(" sequenceNumber : ").append(sequenceNumber)
		.append(PIPE).append(" protocolVersion : ").append(protocolVersion)
		.append(PIPE).append(" syncTime : ").append(syncTime)
		.append(PIPE).append(" beaconLoggerTick : ").append(beaconLoggerTick)
		.append(PIPE).append(" extractTime : ").append(extractTime)
		.append(PIPE).append(" reserved : ").append(reserved)
		.append(PIPE).append(" battery : ").append(battery)
		.append(PIPE).append(" count : ").append(count)
		.append(PIPE).append(" rssi : ").append(rssi)
		.append(PIPE).append(" lastseen : ").append(lastseen)
		.append(("]")).toString();
	}
	
}
