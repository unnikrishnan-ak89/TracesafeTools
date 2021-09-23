package com.tracesafe.subscriber.sanity.checker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.tracesafe.subscriber.sanity.checker.Constants;
import com.tracesafe.subscriber.sanity.checker.pojo.ExecutionData;
import com.tracesafe.subscriber.sanity.checker.pojo.TagProximityPacket;

public class ProximityPacketUtil {
	
	public static Byte[] createPacket(ExecutionData executionData, TagProximityPacket packetData) {
		List<Long> tagArray = Arrays.asList(executionData.getBeaconLoggerId1());
		return createPacket(executionData.getRootOrgId(), executionData.getBridgeSiteId1(), executionData.getBridgeSerialNo1(), executionData.getBeaconLoggerId1(), tagArray, 15L, packetData);
	}
	
	private static Byte[] createPacket(int rootOrgId, int subOrgId, int bridgeId, long beaconlogger, List<Long> tagArray, long interval, TagProximityPacket packetData) {

		int header = Constants.TAG_PACKET_HEADER;
		int count = tagArray.size();

		int i = 0;
		int battery = 255;
		List<Byte> bytearr = new ArrayList<>();

		while (i < count && i < 101) {
			// System.out.println(beaconlogger+":tagarray size"+tagArray.size()+":"+i);
			long tagId = tagArray.get(i);
			bytearr.addAll(tagData(tagId, interval, packetData));
			i++;
		}
		i--;

		int recordNo = i + 2;
		int packetlength = 20 * (i + 2) + 17;
		int seq = packetData.getSequenceNumber();
		int protocol = packetData.getProtocolVersion();
		int recordsize = 20;
		long synctime = packetData.getSyncTime();
		int b0 = 176;
		long beaconloggertick = packetData.getBeaconLoggerTick();
		long extracttime = packetData.getExtractTime();
		long currenttime = System.currentTimeMillis() / 1000;

		Byte[] arr = new Byte[40];
		arr[0] = (byte) (header & 0xff);
		arr[1] = (byte) ((packetlength >> 8) & 0xff);
		arr[2] = (byte) ((packetlength) & 0xff);
		arr[3] = (byte) ((rootOrgId >> 8) & 0xff);
		arr[4] = (byte) ((rootOrgId) & 0xff);
		arr[5] = (byte) ((subOrgId >> 8) & 0xff);
		arr[6] = (byte) ((subOrgId) & 0xff);
		arr[7] = (byte) ((bridgeId >> 8) & 0xff);
		arr[8] = (byte) ((bridgeId) & 0xff);
		arr[9] = (byte) ((beaconlogger >> 24) & 0xff);
		arr[10] = (byte) ((beaconlogger >> 16) & 0xff);
		arr[11] = (byte) ((beaconlogger >> 8) & 0xff);
		arr[12] = (byte) ((beaconlogger) & 0xff);
		arr[13] = (byte) ((seq >> 8) & 0xff);
		arr[14] = (byte) ((seq) & 0xff);
		arr[15] = (byte) ((protocol) & 0xff);
		arr[16] = (byte) ((recordsize) & 0xff);
		arr[17] = (byte) ((recordNo >> 8) & 0xff);
		arr[18] = (byte) ((recordNo) & 0xff);
		arr[19] = (byte) ((battery) & 0xff);
		arr[20] = (byte) ((beaconlogger >> 24) & 0xff);
		arr[21] = (byte) ((beaconlogger >> 16) & 0xff);
		arr[22] = (byte) ((beaconlogger >> 8) & 0xff);
		arr[23] = (byte) ((beaconlogger) & 0xff);
		arr[24] = (byte) ((synctime >> 24) & 0xff);
		arr[25] = (byte) ((synctime >> 16) & 0xff);
		arr[26] = (byte) ((synctime >> 8) & 0xff);
		arr[27] = (byte) ((synctime) & 0xff);
		arr[28] = (byte) ((b0) & 0xff);
		arr[29] = (byte) ((beaconloggertick >> 16) & 0xff);
		arr[30] = (byte) ((beaconloggertick >> 8) & 0xff);
		arr[31] = (byte) ((beaconloggertick) & 0xff);
		arr[32] = (byte) ((extracttime >> 24) & 0xff);
		arr[33] = (byte) ((extracttime >> 16) & 0xff);
		arr[34] = (byte) ((extracttime >> 8) & 0xff);
		arr[35] = (byte) ((extracttime) & 0xff);
		arr[36] = (byte) ((currenttime >> 24) & 0xff);
		arr[37] = (byte) ((currenttime >> 16) & 0xff);
		arr[38] = (byte) ((currenttime >> 8) & 0xff);
		arr[39] = (byte) ((currenttime) & 0xff);

		Byte[] arrtosend = ArrayUtils.addAll(arr, bytearr.toArray(new Byte[0]));
		return arrtosend;
	}

	private static List<Byte> tagData(long tag1, long interval, TagProximityPacket packetData) {

		int beacontype = Constants.B2;
		int reserved = packetData.getReserved();
		int count = packetData.getCount();
		long lastseen = packetData.getLastseen();
		long firstseen = lastseen - interval;
		int rssi = packetData.getRssi();
		int batteryValue = packetData.getBattery();

		Byte[] arr = new Byte[20];

		arr[0] = (byte) ((tag1 >> 24) & 0xff);
		arr[1] = (byte) ((tag1 >> 16) & 0xff);
		arr[2] = (byte) ((tag1 >> 8) & 0xff);
		arr[3] = (byte) ((tag1) & 0xff);
		arr[4] = (byte) ((reserved >> 16) & 0xff);
		arr[5] = (byte) ((reserved >> 8) & 0xff);
		arr[6] = (byte) ((reserved) & 0xff);
		arr[7] = (byte) ((batteryValue) & 0xff);
		arr[8] = (byte) ((beacontype) & 0xff);
		arr[9] = (byte) ((count >> 8) & 0xff);
		arr[10] = (byte) ((count) & 0xff);
		arr[11] = (byte) ((firstseen >> 24) & 0xff);
		arr[12] = (byte) ((firstseen >> 16) & 0xff);
		arr[13] = (byte) ((firstseen >> 8) & 0xff);
		arr[14] = (byte) ((firstseen) & 0xff);
		arr[15] = (byte) ((lastseen >> 24) & 0xff);
		arr[16] = (byte) ((lastseen >> 16) & 0xff);
		arr[17] = (byte) ((lastseen >> 8) & 0xff);
		arr[18] = (byte) ((lastseen) & 0xff);
		arr[19] = (byte) ((rssi) & 0xff);

		return Arrays.asList(arr);
	}

}
