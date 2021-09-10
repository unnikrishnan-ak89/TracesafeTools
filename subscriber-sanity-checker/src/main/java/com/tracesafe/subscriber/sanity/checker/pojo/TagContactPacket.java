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
public class TagContactPacket {
	
	private static final String PIPE = "|";

	private int battery;
	private int sequenceNumber;
	private int protocolVersion;
	private long beaconLoggerTick;
	private long extractTime;
	
	private int guidIndex;
	private int count;
	private int rssi;
	private int contactInterval; // in SECONDS
	
	private boolean randomBatteryEnabled;
	
	public TagContactPacket() {}
	
	public TagContactPacket(int sequenceNumber, int protocolVersion, long beaconLoggerTick,
			long extractTime, int battery, int guidIndex, int count, int rssi, int contactInterval) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.protocolVersion = protocolVersion;
		this.beaconLoggerTick = beaconLoggerTick;
		this.extractTime = extractTime;
		this.battery = battery;
		this.guidIndex = guidIndex;
		this.count = count;
		this.rssi = rssi;
		this.contactInterval = contactInterval;
	}
	
	public TagContactPacket(int sequenceNumber, int protocolVersion, long beaconLoggerTick,
			long extractTime, boolean randomBatteryEnabled, int guidIndex, int count, int rssi, int contactInterval) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.protocolVersion = protocolVersion;
		this.beaconLoggerTick = beaconLoggerTick;
		this.extractTime = extractTime;
		this.randomBatteryEnabled = randomBatteryEnabled;
		this.guidIndex = guidIndex;
		this.count = count;
		this.rssi = rssi;
		this.contactInterval = contactInterval;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[").append(" sequenceNumber : ").append(sequenceNumber)
		.append(PIPE).append(" protocolVersion : ").append(protocolVersion)
		.append(PIPE).append(" beaconLoggerTick : ").append(beaconLoggerTick)
		.append(PIPE).append(" extractTime : ").append(extractTime)
		.append(PIPE).append(" battery : ").append(battery)
		.append(PIPE).append(" guidIndex : ").append(guidIndex)
		.append(PIPE).append(" count : ").append(count)
		.append(PIPE).append(" rssi : ").append(rssi)
		.append(PIPE).append(" contactInterval : ").append(contactInterval)
		.append(("]")).toString();
	}
}
