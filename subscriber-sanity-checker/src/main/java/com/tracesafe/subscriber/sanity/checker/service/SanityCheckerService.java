package com.tracesafe.subscriber.sanity.checker.service;

import org.springframework.stereotype.Service;

import com.tracesafe.subscriber.sanity.checker.pojo.ExecutionData;
import com.tracesafe.subscriber.sanity.checker.type.TestCaseEnum;
import com.tracesafe.subscriber.sanity.checker.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service	
@Slf4j
public class SanityCheckerService {

	private ExecutionData executionData;

	public void initiateTest(String subscribebrSanityJsonPath) {
		LOGGER.info("Initiating sanity with input json : {}", subscribebrSanityJsonPath);
		executionData = (ExecutionData) JsonUtil.getObjectFromFile(subscribebrSanityJsonPath, ExecutionData.class);
		
		LOGGER.info("BridgeId-1 : {}", executionData.getBridgeId1());
		LOGGER.info("TagId-1 : {}", executionData.getTagId1());
		LOGGER.info("TagId-2 : {}", executionData.getTagId2());
		
		for (TestCaseEnum testCaseEnum : TestCaseEnum.values()) {
			executionData.setTestCaseEnum(testCaseEnum);
			
		}
	}
	
	private void evaluateTest() {
		
		switch(executionData.getTestCaseEnum()) {
		case PROXIMITY_USER_CHECKIN_SET:
			
			break;
		default :
			break;
		}
	}
}
