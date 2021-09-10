package com.tracesafe.subscriber.sanity.checker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.tracesafe.subscriber.sanity.checker.Constants;
import com.tracesafe.subscriber.sanity.checker.pojo.TagContactPacket;

public class ContactDataPacketUtil {

	public static Byte[] createPacket(int rootOrgId, int subOrgId, int bridgeId, long beaconlogger, List<Long> tagArray, TagContactPacket packetData, int maxNumberOfRecords) {

		int header = Constants.TAG_PACKET_HEADER;
		int count = tagArray.size();
		Random rand = new Random();
		int i = 0;
		int battery = packetData.getBattery();
		if (packetData.isRandomBatteryEnabled()) {
			battery = rand.nextInt(101);
		}
		List<Byte> bytearr = new ArrayList<>();

		int contactCount = 0;
		while (i < count && i < maxNumberOfRecords) {
			long tagId = tagArray.get(i);
			if (tagId != beaconlogger) {
				bytearr.addAll(tagData(beaconlogger, tagId, packetData));
				contactCount++;
			} else {
				// System.out.println("inside+"+tagId);
			}
			i++;
		}
		i--;

		int recordNo = contactCount + 2;
		int packetlength = 20 * (contactCount + 2) + 17;
		int seq = packetData.getSequenceNumber();
		int protocol = packetData.getProtocolVersion();
		int recordsize = 20;
		long synctime = (System.currentTimeMillis() / 1000) - 2;
		int b0 = Constants.B0;
		long beaconloggertick = packetData.getBeaconLoggerTick();
		long extracttime = (System.currentTimeMillis() / 1000) - 2;
		long currenttime = System.currentTimeMillis() / 1000;
		long devicetype = 8009;
		int hv1 = 1;
		int hv2 = 1;
		int hv3 = 17;
		int fv1 = 1;
		int fv2 = 23;
		int fv3 = 10;
		int sw1 = 1;
		int sw2 = 0;
		int sw3 = 23;
		int reserved = 0;
		int b1 = Constants.B1;
		int devicetypeminor = 0;

		Byte[] arr = new Byte[60];
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
		arr[40] = (byte) ((beaconlogger >> 24) & 0xff);
		arr[41] = (byte) ((beaconlogger >> 16) & 0xff);
		arr[42] = (byte) ((beaconlogger >> 8) & 0xff);
		arr[43] = (byte) ((beaconlogger) & 0xff);
		arr[44] = (byte) ((hv1) & 0xff);
		arr[45] = (byte) ((hv2) & 0xff);
		arr[46] = (byte) ((hv3) & 0xff);
		arr[47] = (byte) ((reserved) & 0xff);
		arr[48] = (byte) ((b1) & 0xff);
		arr[49] = (byte) ((devicetype >> 8) & 0xff);
		arr[50] = (byte) ((devicetype) & 0xff);
		arr[51] = (byte) ((fv1) & 0xff);
		arr[52] = (byte) ((fv2) & 0xff);
		arr[53] = (byte) ((fv3) & 0xff);
		arr[54] = (byte) ((reserved) & 0xff);
		arr[55] = (byte) ((sw1) & 0xff);
		arr[56] = (byte) ((sw2) & 0xff);
		arr[57] = (byte) ((sw3) & 0xff);
		arr[58] = (byte) ((reserved) & 0xff);
		arr[59] = (byte) ((devicetypeminor) & 0xff);

		Byte[] packet = ArrayUtils.addAll(arr, bytearr.toArray(new Byte[0]));
		return packet;
	}

	private static List<Byte> tagData(long tag1, long tag2, TagContactPacket packetData) {

		int interval = packetData.getContactInterval();

		long endTime = (System.currentTimeMillis() / 1000) - interval;
		long startTime = endTime - interval;

		int beacontype = 1;
		int count = packetData.getCount();
		int guidindex = packetData.getGuidIndex();
		int rssi = packetData.getRssi();

		Byte[] arr = new Byte[20];

		arr[0] = (byte) ((tag1 >> 24) & 0xff);
		arr[1] = (byte) ((tag1 >> 16) & 0xff);
		arr[2] = (byte) ((tag1 >> 8) & 0xff);
		arr[3] = (byte) ((tag1) & 0xff);
		arr[4] = (byte) ((tag2 >> 24) & 0xff);
		arr[5] = (byte) ((tag2 >> 16) & 0xff);
		arr[6] = (byte) ((tag2 >> 8) & 0xff);
		arr[7] = (byte) ((tag2) & 0xff);
		arr[8] = (byte) ((beacontype) & 0xff);
		arr[9] = (byte) ((guidindex) & 0xff);
		arr[10] = (byte) ((count) & 0xff);
		arr[11] = (byte) ((startTime >> 24) & 0xff);
		arr[12] = (byte) ((startTime >> 16) & 0xff);
		arr[13] = (byte) ((startTime >> 8) & 0xff);
		arr[14] = (byte) ((startTime) & 0xff);
		arr[15] = (byte) ((endTime >> 24) & 0xff);
		arr[16] = (byte) ((endTime >> 16) & 0xff);
		arr[17] = (byte) ((endTime >> 8) & 0xff);
		arr[18] = (byte) ((endTime) & 0xff);
		arr[19] = (byte) ((rssi) & 0xff);

		return Arrays.asList(arr);
	}

}
