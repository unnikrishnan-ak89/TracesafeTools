/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
public class RedisUtils {
	

	public static final String SANITY_CHECKER_TOPIC = "subscriber-sanity";
	
	public static final String DELIMETER = ":";
	public static final int PHP_CHECKIN_DATA_PROCESS_FLAG = 0;
	
	public static final String DELIMETER_REPLACE_CHAR = "@";
	
	public static final int DB_TAG_CHECKIN_TIME = 51;
	public static final int DB_SUBORG_TIMEZONE = 52;
	public static final int DB_GENERAL_CACHE = 549;
	public static final int DB_LOG_DISABLE_SET = 550;
	
	public static final int DB_BRIDGE_INFO = 509;
	public static final int DB_TAG_INFO = 513;
	
	/* key formats */
	public static final String TAG_CHECKIN_TIME_KEY_FORMAT= "RO%dU%sD%s"; // <RO(RootOrgId)U(userId)D(CurrntDateInSubOrgTimezone)>
	public static final String TAG_CHECKIN_TIME_VALUE_FORMAT= "%s:%s:%d:%d:%s:%s:%d:%d:%d"; // <tagId:userId:siteId:gatewayId:currentDate:checkinTime:lastseen:updateTimestamp:phpProcesFlag> 

	public static final String SUBORG_TIMEZONE_KEY_FORMAT = "RO%sSO%s";  // RO and SO - Left pad woth 0 to make it 5 digit to maintain consistency
	public static final String SUBORG_TIMEZONE_VALUE_FORMAT= "%d:%d:%s"; // <rootOrgId:subOrgId:timezone> >> timezone is in "Asia/Kolkata" format
	
	public static final String TAG_KEEP_ALIVE_KEY_FORMAT= "RO%dSO%dT%s"; // <RO(RootOrgId)SO(subOrgId)T(TagId)>
	public static final String TAG_KEEP_ALIVE_VALUE_FORMAT= "%d:%d:%s:%d:%d:%d:%d"; // <rootOrgId:subOrgId:tagId:lastSyncTime:lastSeenTime:batteryLevel:batteryLevelUpdateTime>
	
	public static final String BRIDGE_INFO_KEY_FORMAT= "RO%dSO%dBI%d"; // <RO(RootOrgId)SO(subOrgId)BI(BridgeId)>
	public static final String BRIDGE_INFO_VALUE_FORMAT= "%d:%d:%d"; // <gwUploadTime:gwHeartBeatTime:gwLoginTime>
	
	
	/* Redis- SANITY_CHECKER_TOPIC channel commands descriptions*/
	public static final String CMD_INITIATE_SANITY_CHECK = "1";

	public static final String replaceDelimeter(String value) {
		return StringUtils.isEmpty(value) ? value : value.replace(DELIMETER, DELIMETER_REPLACE_CHAR);
	}

	public static final String getTagKeepAliveKey(int rootOrgId, long subOrgId, String tagId) {
		return String.format(TAG_KEEP_ALIVE_KEY_FORMAT, rootOrgId, subOrgId, tagId);
	}

	public static final String getTagKeepAliveValue(int rootOrgId, long subOrgId, String tagId, long lastSync, long lastSeen, int batteryLevel, long betteryLevelUpdateTime) {
		return String.format(TAG_KEEP_ALIVE_VALUE_FORMAT, rootOrgId, subOrgId, tagId, lastSync, lastSeen, batteryLevel, betteryLevelUpdateTime);
	}

	public static final String getBridgeInfoKey(int rootOrgId, int subOrgId, int bridgeId) {
		return String.format(BRIDGE_INFO_KEY_FORMAT, rootOrgId, subOrgId, bridgeId);
	}

	public static final String getBridgeInfoValue(long gwUploadTime, long gwHeartBeatTime, long gwLoginTime) {
		return String.format(RedisUtils.BRIDGE_INFO_VALUE_FORMAT, gwUploadTime, gwHeartBeatTime, gwLoginTime);
	}
	
	public static final String reformatValue(String value) {
		return StringUtils.isEmpty(value) ? value : value.replace(DELIMETER_REPLACE_CHAR, DELIMETER);
	}
	
	public static final String stringLeftPad(String string, int size, char padChar) {
		if (null == string) {
			return null;
		}
		return StringUtils.leftPad(string, size, padChar);
	}
	

}
