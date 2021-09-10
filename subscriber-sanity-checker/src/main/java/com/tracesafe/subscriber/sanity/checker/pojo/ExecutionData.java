package com.tracesafe.subscriber.sanity.checker.pojo;

import com.tracesafe.subscriber.sanity.checker.type.TestCaseEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionData extends SanityInput 	{

	private TestCaseEnum testCaseEnum;
	private long executionTime;
	private long initialLastSeen;
}
