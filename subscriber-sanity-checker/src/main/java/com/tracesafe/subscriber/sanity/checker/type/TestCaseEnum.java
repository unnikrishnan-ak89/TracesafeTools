package com.tracesafe.subscriber.sanity.checker.type;

import lombok.Getter;

@Getter
public enum TestCaseEnum {

	PROXIMITY_USER_CHECKIN_SET("PROXIMITY_USER_CHECKIN_SET", "1.1 User Checkin history - Set value"),
	PROXIMITY_USER_CHECKIN_UPDATE("PROXIMITY_USER_CHECKIN_UPDATE", "1.2 User Checkin history - Update value"),
	PROXIMITY_USER_CHECKIN_NOCHANGE_GREATER("PROXIMITY_USER_CHECKIN_NOCHANGE_GREATER", "1.3 Update User Checkin history - No change - Greater lastSeen"),
	PROXIMITY_USER_CHECKIN_NOCHANGE_PREVIOUSDAY("PROXIMITY_USER_CHECKIN_NOCHANGE_PREVIOUSDAY", "1.4 Update User Checkin history - No change - Previous day lastSeen", 100),
	PROXIMITY_UPDATE_TAG_KEEPALIVE("PROXIMITY_UPDATE_TAG_KEEPALIVE", "1.5 Update Tag Keep-Alive"),
	PROXIMITY_UPDATE_BRIDGE_KEEPALIVE("PROXIMITY_UPDATE_BRIDGE_KEEPALIVE", "1.6 Update Bridge Keep-Alive"),
	
	PROXIMITY_BATTERY_SET_100("PROXIMITY_BATTERY_SET_100", "1.7.1 Proximity Battery value Set - Set 100 value", 100),
	PROXIMITY_BATTERY_UPDATE_100_TO_85("PROXIMITY_BATTERY_UPDATE_100_TO_85", "1.7.2 Proximity Battery value Decrease - 100 to 85", 85),
	PROXIMITY_BATTERY_UPDATE_85_TO_71("PROXIMITY_BATTERY_UPDATE_85_TO_71", "1.7.3 Proximity Battery value Decrease - 85 to 71", 71),
	PROXIMITY_BATTERY_UPDATE_71_TO_57("PROXIMITY_BATTERY_UPDATE_71_TO_57", "1.7.4 Proximity Battery value Decrease - 71 to 57", 57),
	PROXIMITY_BATTERY_UPDATE_57_TO_42("PROXIMITY_BATTERY_UPDATE_57_TO_42", "1.7.5 Proximity Battery value Decrease - 57 to 42", 42),
	PROXIMITY_BATTERY_UPDATE_42_TO_28("PROXIMITY_BATTERY_UPDATE_42_TO_28", "1.7.6 Proximity Battery value Decrease - 42 to 28", 28),
	PROXIMITY_BATTERY_UPDATE_28_TO_14("PROXIMITY_BATTERY_UPDATE_28_TO_14", "1.7.7 Proximity Battery value Decrease - 28 to 14", 14),
	PROXIMITY_BATTERY_UPDATE_14_TO_6("PROXIMITY_BATTERY_UPDATE_14_TO_6", "1.7.8 Proximity Battery value Decrease - 14 to 6", 0),
	PROXIMITY_BATTERY_NOCHANGE_6("PROXIMITY_BATTERY_NOCHANGE_6", "1.7.9 Proximity Battery value No change - 6"),
	PROXIMITY_BATTERY_UPDATE_6_TO_14("PROXIMITY_BATTERY_UPDATE_6_TO_14", "1.7.10 Proximity Battery value Increase - 6 to 14", 14),
	PROXIMITY_BATTERY_UPDATE_14_TO_28("PROXIMITY_BATTERY_UPDATE_14_TO_28", "1.7.11 Proximity Battery value Increase - 14 to 28", 28),
	PROXIMITY_BATTERY_UPDATE_28_TO_42("PROXIMITY_BATTERY_UPDATE_28_TO_42", "1.7.12 Proximity Battery value Increase - 28 to 42", 42),
	PROXIMITY_BATTERY_UPDATE_42_TO_57("PROXIMITY_BATTERY_UPDATE_42_TO_57", "1.7.13 Proximity Battery value Increase - 42 to 57", 57),
	PROXIMITY_BATTERY_UPDATE_57_TO_71("PROXIMITY_BATTERY_UPDATE_57_TO_71", "1.7.14 Proximity Battery value Increase - 57 to 71", 71),
	PROXIMITY_BATTERY_UPDATE_71_TO_85("PROXIMITY_BATTERY_UPDATE_71_TO_85", "1.7.15 Proximity Battery value Increase - 71 to 85", 85),
	PROXIMITY_BATTERY_UPDATE_85_TO_100("PROXIMITY_BATTERY_UPDATE_85_TO_100", "1.7.16 Proximity Battery value Increase - 85 to 100", 100),
	;
	
	private final String key;
	private final String description;
	private final Integer batteryValue;

	TestCaseEnum(String key, String description) {
        this.key = key;
        this.description = description;
        this.batteryValue = null;
    }

	TestCaseEnum(String key, String description, Integer batteryValue) {
        this.key = key;
        this.description = description;
        this.batteryValue = batteryValue;
    }
    
    public static TestCaseEnum getByKey(String key) {
    	for(TestCaseEnum en : values()) {
    		if (en.getKey().equals(key)) {
    			return en;
    		}
    	}
    	return null;
    }
}
