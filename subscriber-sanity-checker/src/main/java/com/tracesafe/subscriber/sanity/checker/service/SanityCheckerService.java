package com.tracesafe.subscriber.sanity.checker.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tracesafe.subscriber.sanity.checker.config.MqttConnect;
import com.tracesafe.subscriber.sanity.checker.model.EvaluateData;
import com.tracesafe.subscriber.sanity.checker.model.ReportData;
import com.tracesafe.subscriber.sanity.checker.pojo.ExecutionData;
import com.tracesafe.subscriber.sanity.checker.pojo.TagContactPacket;
import com.tracesafe.subscriber.sanity.checker.pojo.TagProximityPacket;
import com.tracesafe.subscriber.sanity.checker.type.ConfigEnum;
import com.tracesafe.subscriber.sanity.checker.type.TestCaseEnum;
import com.tracesafe.subscriber.sanity.checker.utils.CommonUtils;
import com.tracesafe.subscriber.sanity.checker.utils.ContactDataPacketUtil;
import com.tracesafe.subscriber.sanity.checker.utils.CsvUtil;
import com.tracesafe.subscriber.sanity.checker.utils.DateUtil;
import com.tracesafe.subscriber.sanity.checker.utils.FileUtils;
import com.tracesafe.subscriber.sanity.checker.utils.JsonUtil;
import com.tracesafe.subscriber.sanity.checker.utils.ProximityPacketUtil;
import com.tracesafe.subscriber.sanity.checker.utils.RedisUtils;

import lombok.extern.slf4j.Slf4j;

@Service	
@Slf4j
public class SanityCheckerService {
	
	private static long UPDATE_LAST_SEEN_DIFF = 100; 
	
	private static long ONEDAY_LAST_SEEN_DIFF = 86400; // 60 x 60 x 24 = 86,400 
	
	private List<TestCaseEnum> retriedTests = new ArrayList<>();

	@Value("${subscriber.sanity.checker.test.report.path}")
	private String reportDirectoryPath;
	
	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	@Qualifier("tagCheckinTimeTemplate")
	private StringRedisTemplate tagCheckinTimeTemplate;	 // 51

	@Autowired
	@Qualifier("subOrgTimezoneTemplate")
	private StringRedisTemplate subOrgTimezoneTemplate;  // 52
	
	@Autowired
	@Qualifier("taginfotemplate")
	StringRedisTemplate tagKeepaliveTemplate; // 513 - updating last_seen/ last sync / battery value
	
	@Autowired
	@Qualifier("bridgeinfotemplate")
	StringRedisTemplate bridgeKeepaliveTemplate; // 509

	private ExecutionData executionData;
	
	private TagProximityPacket proximityPacket = null;
	
	private TagContactPacket tagContactPacket = null;

	private MqttAsyncClient mqttClientBridge1;

	private boolean evaluateTestcaseStatus;

	private EvaluateData evaluateData = new EvaluateData(); 
	
	public void initiateTest(String subscribebrSanityJsonPath) {
		LOGGER.info("Initiating tests using subscribebrSanityJsonPath : {}", subscribebrSanityJsonPath);
		executionData = (ExecutionData) JsonUtil.getObjectFromFile(subscribebrSanityJsonPath, ExecutionData.class);
		executionData.setRootOrgId(configurationService.getRootOrgId());
		retriedTests.clear();
		
		TestCaseEnum[] testCases = TestCaseEnum.values();
		testCases = new TestCaseEnum[]{TestCaseEnum.CT_UPDATE_BRIDGEKEEP_ALIVE_ON_INVALID_TAG_PACKET};
		for (TestCaseEnum testCaseEnum : testCases) {
			executionData.setTestCaseEnum(testCaseEnum);
			executeTest();
			evaluateTest();
		}
		
		LOGGER.info("All tests completed...");
		CsvUtil.writeReport(reportDirectoryPath);
	}
	
	private void executeTest() {

		executionData.setExecutionTime(System.currentTimeMillis()/1000);
		
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
			String proximityUserCheckinKey = getProximityUserCheckinKey();
			tagCheckinTimeTemplate.delete(proximityUserCheckinKey);
			publichProximityPacket();
			break;
		case PROXIMITY_USER_CHECKIN_UPDATE:
		case PROXIMITY_USER_CHECKIN_NOCHANGE_GREATER:
		case PROXIMITY_USER_CHECKIN_NOCHANGE_PREVIOUSDAY:	
			publichProximityPacket();
			break;
		case PROXIMITY_UPDATE_TAG_KEEPALIVE:
			String tagKeepAliveKey = getTagKeepAliveKey();
			tagKeepaliveTemplate.delete(tagKeepAliveKey);
			publichProximityPacket();
			waitForSec(3);
			break;
		case PROXIMITY_BATTERY_SET_100:
		case PROXIMITY_BATTERY_UPDATE_100_TO_85:
		case PROXIMITY_BATTERY_UPDATE_85_TO_71:
		case PROXIMITY_BATTERY_UPDATE_71_TO_57:
		case PROXIMITY_BATTERY_UPDATE_57_TO_42:
		case PROXIMITY_BATTERY_UPDATE_42_TO_28:
		case PROXIMITY_BATTERY_UPDATE_28_TO_14:
		case PROXIMITY_BATTERY_UPDATE_14_TO_6:
		case PROXIMITY_BATTERY_NOCHANGE_6:
		case PROXIMITY_BATTERY_UPDATE_6_TO_14:
		case PROXIMITY_BATTERY_UPDATE_14_TO_28:
		case PROXIMITY_BATTERY_UPDATE_28_TO_42:
		case PROXIMITY_BATTERY_UPDATE_42_TO_57:
		case PROXIMITY_BATTERY_UPDATE_57_TO_71:
		case PROXIMITY_BATTERY_UPDATE_71_TO_85:
		case PROXIMITY_BATTERY_UPDATE_85_TO_100:
			publichProximityPacket();
			waitForSec(3);
			break;
		case PROXIMITY_UPDATE_BRIDGE_KEEPALIVE:
			String bridgeKeepAliveKey = getBridgeKeepAliveKey();
			bridgeKeepaliveTemplate.delete(bridgeKeepAliveKey);
			publichProximityPacket();
			break;
		case CT_UPDATE_BRIDGEKEEP_ALIVE_ON_INVALID_TAG_PACKET:
			publichContactPacket();
			break;
		default :
			break;
		}
		
		waitForSec(5);
	}
	
	private void waitForSec(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void publichContactPacket() {
		loadTagContactPacket();
		if (null == mqttClientBridge1) {
			mqttClientBridge1 = getMqttClient(executionData.getBridgeSerialNo1(), executionData.getBridgeId1());
		}
		
		Byte[] arr = ContactDataPacketUtil.createPacket(executionData, tagContactPacket);
		MqttMessage sendMessage = new MqttMessage(ArrayUtils.toPrimitive(arr));
//		sendMessage.setQos(1); // will wait for acknowledgement
		sendMessage.setQos(0); // will not wait for acknowledgement
        if (mqttClientBridge1 != null && mqttClientBridge1.isConnected()) {
        	String topic = String.format("1/cmd/%d/%d", executionData.getRootOrgId(), executionData.getBridgeSiteId1());
            try {
            	mqttClientBridge1.publish(topic, sendMessage);
            } catch (Exception e) {
            	LOGGER.error("Exception while publishing proximityPackey to topic : {}", topic, e);
            }
        }
	}
	
	private void publichProximityPacket() {
		loadProximityPacket();
		if (null == mqttClientBridge1) {
			mqttClientBridge1 = getMqttClient(executionData.getBridgeSerialNo1(), executionData.getBridgeId1());
		}
		
		Byte[] arr = ProximityPacketUtil.createPacket(executionData, proximityPacket);
		MqttMessage sendMessage = new MqttMessage(ArrayUtils.toPrimitive(arr));
//		sendMessage.setQos(1); // will wait for acknowledgement
		sendMessage.setQos(0); // will not wait for acknowledgement
        if (mqttClientBridge1 != null && mqttClientBridge1.isConnected()) {
        	String topic = String.format("1/cmd/%d/%d", executionData.getRootOrgId(), executionData.getBridgeSiteId1());
            try {
            	mqttClientBridge1.publish(topic, sendMessage);
            } catch (Exception e) {
            	LOGGER.error("Exception while publishing proximityPackey to topic : {}", topic, e);
            }
        }
	}
	
	private boolean evaluateTest() {
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
		case PROXIMITY_USER_CHECKIN_UPDATE:
		case PROXIMITY_USER_CHECKIN_NOCHANGE_GREATER:
		case PROXIMITY_USER_CHECKIN_NOCHANGE_PREVIOUSDAY:	
			evaluateTestcaseStatus = checkProximityPacketCachedUserCheckinTime();
			break;
		case PROXIMITY_UPDATE_TAG_KEEPALIVE:
		case PROXIMITY_BATTERY_SET_100:
		case PROXIMITY_BATTERY_UPDATE_100_TO_85:
		case PROXIMITY_BATTERY_UPDATE_85_TO_71:
		case PROXIMITY_BATTERY_UPDATE_71_TO_57:
		case PROXIMITY_BATTERY_UPDATE_57_TO_42:
		case PROXIMITY_BATTERY_UPDATE_42_TO_28:
		case PROXIMITY_BATTERY_UPDATE_28_TO_14:
		case PROXIMITY_BATTERY_UPDATE_14_TO_6:
		case PROXIMITY_BATTERY_NOCHANGE_6:
		case PROXIMITY_BATTERY_UPDATE_6_TO_14:
		case PROXIMITY_BATTERY_UPDATE_14_TO_28:
		case PROXIMITY_BATTERY_UPDATE_28_TO_42:
		case PROXIMITY_BATTERY_UPDATE_42_TO_57:
		case PROXIMITY_BATTERY_UPDATE_57_TO_71:
		case PROXIMITY_BATTERY_UPDATE_71_TO_85:
		case PROXIMITY_BATTERY_UPDATE_85_TO_100:
			evaluateTestcaseStatus = checkTagKeepAliveUpdate();
			if(!evaluateTestcaseStatus) {
				evaluateTestcaseStatus = reEvaluateTagKeepAliveUpdate();
			}
			break;
		case PROXIMITY_UPDATE_BRIDGE_KEEPALIVE:
			evaluateTestcaseStatus = checkBridgeKeepAliveUpdate();
			if(!evaluateTestcaseStatus) {
				evaluateTestcaseStatus = reEvaluateBridgeKeepAliveUpdate();
			}
			break;
		case CT_UPDATE_BRIDGEKEEP_ALIVE_ON_INVALID_TAG_PACKET:
			evaluateTestcaseStatus = checkBridgeKeepAliveUpdate();
			if(!evaluateTestcaseStatus) {
				evaluateTestcaseStatus = reEvaluateBridgeKeepAliveUpdate();
			}
			executionData.setBeaconLoggerId1(executionData.getInitialBeaconLoggerId1());
			break;
		default:
			break;
		}
		LOGGER.info("evaluating test : {} >> {}", executionData.getTestCaseEnum().getKey(), evaluateTestcaseStatus ? "SUCCESS" : "FAILED");
		addToReport();
		return evaluateTestcaseStatus;
	}
	
	private void addToReport() {
		ReportData data = new ReportData();
		data.setTestCase(executionData.getTestCaseEnum().getDescription());
		data.setStatus(evaluateData.isSuccess() ? "SUCCESS" : "FAILED");
		data.setCache(String.valueOf(executionData.getTestCaseEnum().getCache()));
		data.setKeyValue(String.format("Key : %s -> Value : %s", evaluateData.getKey(), evaluateData.getValue()));
		data.setExtraInfo(evaluateData.getExtraInfo());
		CsvUtil.saveReportData(data);
	}
	
	private boolean reEvaluateTagKeepAliveUpdate() {
		if(!retriedTests.contains(executionData.getTestCaseEnum())) {
			retriedTests.add(executionData.getTestCaseEnum());
			waitForSec(3);
			return checkTagKeepAliveUpdate();
		}
		return false;
	}
	
	private boolean reEvaluateBridgeKeepAliveUpdate() {
		if(!retriedTests.contains(executionData.getTestCaseEnum())) {
			retriedTests.add(executionData.getTestCaseEnum());
			waitForSec(3);
			return checkBridgeKeepAliveUpdate();
		}
		return false;
	}
	
	private String getProximityUserCheckinKey() {
		String timezone = getSiteTimezone(executionData.getRootOrgId(), executionData.getBridgeSiteId1());
		String currentDate = DateUtil.getCurretDate(DateUtil.DEFAULT_DATE_FORMAT, timezone);
		return String.format(RedisUtils.TAG_CHECKIN_TIME_KEY_FORMAT, executionData.getRootOrgId(), executionData.getTagUser1(), currentDate);
	}
	
	private String getTagKeepAliveKey() {
		return RedisUtils.getTagKeepAliveKey(executionData.getRootOrgId(), executionData.getBridgeSiteId1(), executionData.getTagId1());
	}
	
	private String getBridgeKeepAliveKey() {
		return RedisUtils.getBridgeInfoKey(executionData.getRootOrgId(), executionData.getBridgeSiteId1(), executionData.getBridgeSerialNo1());
	}
	
	private boolean checkTagKeepAliveUpdate() {
		String key = null, value = null;
		try {
			key = getTagKeepAliveKey();
			value = tagKeepaliveTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				LOGGER.info("**************************** checkTagKeepAliveUpdate failed, value found null against key : {}", key);
				setEvaluateData(false, key, "NULL", null);
				return false;
			}
			
			int expectedBatteryValue = null != executionData.getTestCaseEnum().getBatteryValue() ? executionData.getTestCaseEnum().getBatteryValue().intValue() : proximityPacket.getBattery();
			if(TestCaseEnum.PROXIMITY_BATTERY_UPDATE_14_TO_6.equals(executionData.getTestCaseEnum()) || TestCaseEnum.PROXIMITY_BATTERY_NOCHANGE_6.equals(executionData.getTestCaseEnum())) {
				expectedBatteryValue = 6;			
			}
			String[] tagInfo = value.split(":");
			long cachedLastSeen = Long.parseLong(tagInfo[5]);
			int cachedBatteryValue = Integer.parseInt(tagInfo[6]);
			boolean lastSeenCacheUpdated = cachedLastSeen >= executionData.getExecutionTime();
			boolean batteryCacheUpdated =  expectedBatteryValue == cachedBatteryValue;
			if (lastSeenCacheUpdated && batteryCacheUpdated) {
				setEvaluateData(true, key, value, null);
				return true;
			}
//			LOGGER.info("**************************** lastSeenCacheUpdated : {} && batteryCacheUpdated : {}", lastSeenCacheUpdated, batteryCacheUpdated);
			setEvaluateData(false, key, value, null);
		} catch (Exception e) {
			LOGGER.error("Exception while processing checkTagKeepAliveUpdate() ", e);
			setEvaluateData(false, key, value, "Exception while processing checkTagKeepAliveUpdate");
		}
		
		return false;
	}
	
	private void setEvaluateData(boolean success, String key, String value, String extraInfo) {
		evaluateData.setSuccess(success);
		evaluateData.setKey(key);
		evaluateData.setValue(value);
		evaluateData.setExtraInfo(extraInfo);
	}
	
	private boolean checkBridgeKeepAliveUpdate() {
		String bridgeKeepAliveKey = null, bridgeInfoValue = null;
		try {
			bridgeKeepAliveKey = getBridgeKeepAliveKey();
			bridgeInfoValue = bridgeKeepaliveTemplate.opsForValue().get(bridgeKeepAliveKey);
			if(StringUtils.isBlank(bridgeInfoValue)) {
				LOGGER.info("**************************** checkBridgeKeepAliveUpdate failed, value found null against key : {}", bridgeKeepAliveKey);
				setEvaluateData(false, bridgeKeepAliveKey, "NULL", null);
				return false;
			}
			String[] str = bridgeInfoValue.split(":");		// <gwUploadTime:gwHeartBeatTime:gwLoginTime>
			long gwUploadTime = Long.parseLong(str[0]);
			long gwHeartBeatTime = Long.parseLong(str[1]);
			boolean gwUploadTimeUpdated = gwUploadTime >= executionData.getExecutionTime();
			boolean gwHeartBeatTimeUpdated = gwHeartBeatTime >= executionData.getExecutionTime();
			if (gwUploadTimeUpdated && gwHeartBeatTimeUpdated) {
				setEvaluateData(true, bridgeKeepAliveKey, bridgeInfoValue, null);
				return true;
			}
//			LOGGER.info("**************************** gwUploadTimeUpdated : {} && gwHeartBeatTimeUpdated : {}", gwUploadTimeUpdated, gwHeartBeatTimeUpdated);
			setEvaluateData(false, bridgeKeepAliveKey, bridgeInfoValue, null);
		} catch (Exception e) {
			LOGGER.error("Exception while processing checkBridgeKeepAliveUpdate() ", e);
			setEvaluateData(false, bridgeKeepAliveKey, bridgeInfoValue, "Exception while processing checkBridgeKeepAliveUpdate");
		}
		return false;
	}
	
	private boolean checkProximityPacketCachedUserCheckinTime() {
		String key = null, cacheValue = null;
		try {
			key = getProximityUserCheckinKey();
			cacheValue = tagCheckinTimeTemplate.opsForValue().get(key);
			if(StringUtils.isBlank(cacheValue)) {
				setEvaluateData(false, key, "NULL", null);
				return false;
			}
			
			String[] split = cacheValue.split(":");
			String cachedCheckinValue = split.length != 9 ? null : split[6];
			
			if (null != cachedCheckinValue) {
				long cachedCheckinTime = 0;
				cachedCheckinTime = Long.parseLong(cachedCheckinValue);
				if(executionData.getExpectedLastSeen() == cachedCheckinTime) {
					setEvaluateData(true, key, cacheValue, null);
					return true;
				}
//				LOGGER.warn("evaluating test : {} FAILED >> ExpectedCheckin : {} && cachedCheckinTime : {}", executionData.getTestCaseEnum().getKey(), executionData.getExpectedLastSeen(), cachedCheckinTime);
				setEvaluateData(false, key, cacheValue, null);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while processing checkProximityPacketCachedUserCheckinTime()");
			setEvaluateData(false, key, cacheValue, "Exception while processing checkProximityPacketCachedUserCheckinTime");
		}
		return false;
	}
	
	private String getSiteTimezone(long rootOrgId, long subOrgId) {
		String rootOrgIdStr = RedisUtils.stringLeftPad(String.valueOf(rootOrgId), 5, '0');
		String subOrgIdStr = RedisUtils.stringLeftPad(String.valueOf(subOrgId), 5, '0');
		String key = String.format(RedisUtils.SUBORG_TIMEZONE_KEY_FORMAT, rootOrgIdStr, subOrgIdStr);
		String cacheValue = subOrgTimezoneTemplate.opsForValue().get(key);
		if(null == cacheValue || StringUtils.isEmpty(cacheValue)) {
			LOGGER.debug("SUBORG_TIMEZONE Value not found in cache : {} with key : {}", RedisUtils.DB_SUBORG_TIMEZONE, key);
			return null;
		} 
		String[] split = cacheValue.split(RedisUtils.DELIMETER);  // Format : RedisUtils.SUBORG_TIMEZONE_VALUE_FORMAT
		String timezone = split[2];
		return timezone;
	}
	
	private void loadProximityPacket() {
		if (null == proximityPacket) {
			String csvFilePath = configurationService.getConfigValue(ConfigEnum.TAG_PROXIMITY_PACKET_DATA_FILE);
			InputStream csvDataStream = FileUtils.loadFileStream(csvFilePath);
			if (null == csvDataStream) {
				LOGGER.error("Could not load csvDataStream from configured csvFilePath : {}. Skipping packet simulation", csvFilePath);
				return;
			}
			
			proximityPacket = CommonUtils.getTagProximityPacketData(csvDataStream);
			proximityPacket.setLastseen(System.currentTimeMillis()/1000);
		}
		
		Integer batteryValue = executionData.getTestCaseEnum().getBatteryValue();
		if (null != batteryValue) {
			proximityPacket.setBattery(batteryValue);
		}
		
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
			executionData.setInitialLastSeen(proximityPacket.getLastseen());
			executionData.setExpectedLastSeen(proximityPacket.getLastseen());
			break;
		case PROXIMITY_USER_CHECKIN_UPDATE:
			proximityPacket.setLastseen(executionData.getExpectedLastSeen() - UPDATE_LAST_SEEN_DIFF);
			executionData.setExpectedLastSeen(proximityPacket.getLastseen());
			break;
		case PROXIMITY_USER_CHECKIN_NOCHANGE_GREATER:
			proximityPacket.setLastseen(executionData.getExpectedLastSeen() + ONEDAY_LAST_SEEN_DIFF);
			break;
		case PROXIMITY_USER_CHECKIN_NOCHANGE_PREVIOUSDAY:	
			proximityPacket.setLastseen(executionData.getExpectedLastSeen() - ONEDAY_LAST_SEEN_DIFF);
			break;
		default :
			proximityPacket.setLastseen(System.currentTimeMillis()/1000);
			break;
		}
	}
	
	private void loadTagContactPacket() {
		if (null == tagContactPacket) {
			String csvFilePath = configurationService.getConfigValue(ConfigEnum.TAG_CONTACT_PACKET_DATA_FILE);
			InputStream csvDataStream = FileUtils.loadFileStream(csvFilePath);
			if (null == csvDataStream) {
				LOGGER.error("Could not load csvDataStream from configured csvFilePath : {}. Skipping packet simulation", csvFilePath);
				return;
			}
			
			tagContactPacket = CommonUtils.getTagContactPacketData(csvDataStream);
		}
		
		Integer batteryValue = executionData.getTestCaseEnum().getBatteryValue();
		if (null != batteryValue) {
			tagContactPacket.setBattery(batteryValue);
		}
		
		switch(executionData.getTestCaseEnum()) {
		case CT_UPDATE_BRIDGEKEEP_ALIVE_ON_INVALID_TAG_PACKET:
			executionData.setInitialBeaconLoggerId1(executionData.getBeaconLoggerId1());
			executionData.setBeaconLoggerId1(200);
			break;
		default :
			break;
		}
	}

    public MqttAsyncClient getMqttClient(int bridgeSerialNo, String bridgeId){
    	String bridgeKey = bridgeId.substring(bridgeId.length() - 12);
    	String username = String.format("%s@%s", bridgeKey, bridgeKey);
		String password = String.format("%s@123", bridgeKey);
		String mqttUrl = configurationService.getConfigValue(ConfigEnum.MQTT_URL);
		MqttConnect connect = new MqttConnect(mqttUrl, String.valueOf(bridgeSerialNo), username, password);
		MqttAsyncClient client = connect.register();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		return client;
    }
}
