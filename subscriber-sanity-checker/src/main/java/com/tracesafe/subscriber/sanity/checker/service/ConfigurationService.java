package com.tracesafe.subscriber.sanity.checker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.tracesafe.subscriber.sanity.checker.type.ConfigEnum;

@Service
public class ConfigurationService {
	
	@Autowired
	private Environment environment;
	
	public String getConfigValue(ConfigEnum en) {
    	return environment.getProperty(en.getKey());
    }
	
	public int getRootOrgId() {
    	return Integer.parseInt(getConfigValue(ConfigEnum.ROOT_ORG_ID));
    }
}
