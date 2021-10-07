package com.tracesafe.subscriber.sanity.checker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvaluateData {

	private boolean success;
	private String key = "";
	private String value = "";
	private String extraInfo = "";
	
}
