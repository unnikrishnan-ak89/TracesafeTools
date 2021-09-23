package com.tracesafe.subscriber.sanity.checker.service;

import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tracesafe.subscriber.sanity.checker.config.MqttConnect;
import com.tracesafe.subscriber.sanity.checker.pojo.ExecutionData;
import com.tracesafe.subscriber.sanity.checker.pojo.TagProximityPacket;
import com.tracesafe.subscriber.sanity.checker.type.ConfigEnum;
import com.tracesafe.subscriber.sanity.checker.type.TestCaseEnum;
import com.tracesafe.subscriber.sanity.checker.utils.CommonUtils;
import com.tracesafe.subscriber.sanity.checker.utils.DateUtil;
import com.tracesafe.subscriber.sanity.checker.utils.FileUtils;
import com.tracesafe.subscriber.sanity.checker.utils.JsonUtil;
import com.tracesafe.subscriber.sanity.checker.utils.ProximityPacketUtil;
import com.tracesafe.subscriber.sanity.checker.utils.RedisUtils;

import lombok.extern.slf4j.Slf4j;

@Service	
@Slf4j
public class SanityCheckerService {
	
	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	@Qualifier("tagCheckinTimeTemplate")
	private StringRedisTemplate tagCheckinTimeTemplate;	 // 51

	@Autowired
	@Qualifier("subOrgTimezoneTemplate")
	private StringRedisTemplate subOrgTimezoneTemplate;  // 52

	private ExecutionData executionData;
	
	private TagProximityPacket proximityPacket = null;

	private MqttAsyncClient mqttClientBridge1;

	private boolean evaluateTestcaseStatus; 

	public void initiateTest(String subscribebrSanityJsonPath) {
		executionData = (ExecutionData) JsonUtil.getObjectFromFile(subscribebrSanityJsonPath, ExecutionData.class);
		executionData.setRootOrgId(configurationService.getRootOrgId());
		
		for (TestCaseEnum testCaseEnum : TestCaseEnum.values()) {
			executionData.setTestCaseEnum(testCaseEnum);
			executeTest();
			
			evaluateTest();
			if(true) {
				break;
			}
		}
	}
	
	private void executeTest() {
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
			String proximityUserCheckinKey = getProximityUserCheckinKey();
			tagCheckinTimeTemplate.delete(proximityUserCheckinKey);
			executionData.setExecutionTime(System.currentTimeMillis()/1000);
			publichProximityPacket();
			break;
		default :
			break;
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
	
	private void evaluateTest() {
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
			evaluateTestcaseStatus = checkProximityUserCheckinSet();
			break;
		default :
			break;
		}
		if (evaluateTestcaseStatus) {
			LOGGER.debug("evaluating test : {} >> {}", executionData.getTestCaseEnum().getKey(), evaluateTestcaseStatus ? "SUCCESS" : "FAILED");
		}
	}
	
	private String getProximityUserCheckinKey() {
		String timezone = getSiteTimezone(executionData.getRootOrgId(), executionData.getBridgeSiteId1());
		String currentDate = DateUtil.getCurretDate(DateUtil.DEFAULT_DATE_FORMAT, timezone);
		return String.format(RedisUtils.TAG_CHECKIN_TIME_KEY_FORMAT, executionData.getRootOrgId(), executionData.getTagUser1(), currentDate);
	}
	
	private boolean checkProximityUserCheckinSet() {
		String key = getProximityUserCheckinKey();
		String cacheValue = tagCheckinTimeTemplate.opsForValue().get(key);
		if(StringUtils.isBlank(cacheValue)) {
			return false;
		}
		
		String[] split = cacheValue.split(":");
		String cachedCheckinValue = split.length != 9 ? null : split[6];
		
		if (null != cachedCheckinValue) {
			long cachedLastSeen = 0;
			try {
				cachedLastSeen = Long.parseLong(cachedCheckinValue);
				return (proximityPacket.getLastseen() == cachedLastSeen);
			} catch (NumberFormatException e) {
				LOGGER.error("NumberFormatException while taking cachedCheckinValue : {} from cache 51", cachedCheckinValue);
			}
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
