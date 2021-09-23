/**
 * [2014] - [2019] WiSilica Incorporated  All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of WiSilica Incorporated and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to WiSilica Incorporated
 *  and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *  from WiSilica Incorporated.
 **/
package com.tracesafe.subscriber.sanity.checker.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Unnikrishnan A.K <unnikrishnanak@wisilica.com>
 *
 */
public class DateUtil {
	
	public static final String DEFAULT_DATE_FORMAT= "YYYY-MM-dd";
	
	public static final String ISO_8601_FORMAT= "yyyy-MM-dd'T'HH:mm:ssXXX";

	public static String format(long timeInMilliSec, String dateFormat, String timezone) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		return sdf.format(new Date(timeInMilliSec));
	}
	
	public static String getCurretDate(String dateFormat, String timezone) {
		return format(System.currentTimeMillis(), dateFormat, timezone);
	}
	
	public static long getRemainTimeOnSameDay(long currentTimeInMilliSec, String timezone, ChronoUnit unit, int bufferTimeValue, ChronoUnit bufferUnit) {
		Date fromDate = new Date(currentTimeInMilliSec);
		ZoneId zoneId = TimeZone.getTimeZone(timezone).toZoneId();
		
        LocalDateTime midnight = LocalDateTime.ofInstant(fromDate.toInstant(), zoneId)
        		.plusDays(1)
        		.withHour(ChronoUnit.HOURS.equals(bufferUnit) ? bufferTimeValue : 0)
        		.withMinute(ChronoUnit.MINUTES.equals(bufferUnit) ? bufferTimeValue : 0)
                .withSecond(ChronoUnit.SECONDS.equals(bufferUnit) ? bufferTimeValue : 0)
                .withNano(ChronoUnit.NANOS.equals(bufferUnit) ? bufferTimeValue : 0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(fromDate.toInstant(),
        		zoneId);
        return unit.between(currentDateTime, midnight);
    }
	
	public static boolean isSameDay(long time1InSec, long time2InSec, String timezone) {
		int time1Day = getDayOfMonth(time1InSec, timezone);
		int time2Day = getDayOfMonth(time2InSec, timezone);
		return time1Day == time2Day;
	}
	
	public static int getDayOfMonth(long time1InSec, String timezone) {
		java.util.Date time = new java.util.Date((long) time1InSec * 1000);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(time);
		cal1.setTimeZone(TimeZone.getTimeZone(timezone));
		return cal1.get(Calendar.DAY_OF_MONTH);
	}
}
