package com.tracesafe.subscriber.sanity.checker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportData {

	private String testCase;
	private String status;
	private String cache;
	private String keyValue;
	private String extraInfo;
	
	public ReportData(String testCase, String status, String cache, String keyValue, String extraInfo) {
		super();
		this.testCase = testCase;
		this.status = status;
		this.cache = cache;
		this.keyValue = keyValue;
		this.extraInfo = extraInfo;
	}
	
	public ReportData(String testCase, String status, String cache, String keyValue) {
		super();
		this.testCase = testCase;
		this.status = status;
		this.cache = cache;
		this.keyValue = keyValue;
		this.extraInfo = "";
	}
}
