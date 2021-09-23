package com.tracesafe.subscriber.sanity.checker.type;

import lombok.Getter;

@Getter
public enum ConfigEnum {
	
	TAG_PROXIMITY_PACKET_DATA_FILE("tag.proximity.packet.data.file.path"),
	TAG_CONTACT_PACKET_DATA_FILE("tag.contact.packet.data.file.path"),
	
	ROOT_ORG_ID("sanity.checker.rootOrgId"),
	MQTT_URL("mqtt.url"),
	;
	
	private final String key;

	ConfigEnum(String key) {
        this.key = key;
    }
    
    public static ConfigEnum getByKey(String key) {
    	for(ConfigEnum en : values()) {
    		if (en.getKey().equals(key)) {
    			return en;
    		}
    	}
    	return null;
    }
}
