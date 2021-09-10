package com.tracesafe.subscriber.sanity.checker.pojo;

import com.tracesafe.subscriber.sanity.checker.utils.JsonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter	
public class SanityInput {

	private String tagId1;
	private long tagSerialNo1;
	private String tagUser1;
	
	private String tagId2;
	private long tagSerialNo2;
	private String tagUser2;

	private String bridgeId1;
	private long bridgeSerialNo1;
	private long bridgeSiteId1;

	private String bridgeId2;
	private long bridgeSerialNo2;
	private long bridgeSiteId2;
	
	
//	public static void main(String[] args) {
//		
//		SanityInput input = new SanityInput();
//		
//		input.setTagId1("TTAC:0000000203");
//		input.setTagSerialNo1(4);
//		input.setTagUser1("USER203");
//
//		input.setTagId2("TTAC:0000000204");
//		input.setTagSerialNo2(6);
//		input.setTagUser2("USER204");
//		
//		input.setBridgeId1("001000000d000000000038426d99ac24");
//		input.setBridgeSerialNo1(1);
//		input.setBridgeSiteId1(2);
//		
//		input.setBridgeId2("001000000d000000000048943508705d");
//		input.setBridgeSerialNo2(2);
//		input.setBridgeSiteId2(2);
//		
//		String json = JsonUtil.getJson(input);
//		System.out.println(json);
//	}
 
}
