package com.tracesafe.subscriber.sanity.checker.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVWriter;
import com.tracesafe.subscriber.sanity.checker.model.ReportData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtil {

	public static final String[] REPORT_HEADER = new String[]{"TestCase", "Status", "cache", "key-value", "extraInfo"}; 
	
	public static final String REPORT_NAME_PATTERN = "%s - SanityReport.csv"; 
	
	private static final List<ReportData> REPORT_DATA = new ArrayList<>();
	
	public static String getReportName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return String.format(REPORT_NAME_PATTERN, sdf.format(new Date()));
	}
	
	public static void saveReportData(ReportData data) {
		REPORT_DATA.add(data);
	}
	
	public static void writeReport(String path) {
		if (REPORT_DATA.isEmpty()) {
			LOGGER.info("No data to write...");
			return;
		}
		
		if (!path.endsWith(FileSystems.getDefault().getSeparator())) {
			path += FileSystems.getDefault().getSeparator();
		}
		path += getReportName();
		LOGGER.info("Writing Test Report to path : {}", path);
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeAll(getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		REPORT_DATA.clear();
	}

	private static List<String[]> getData() {
		List<String[]> records = new ArrayList<String[]>();
		records.add(REPORT_HEADER);
		Iterator<ReportData> it = REPORT_DATA.iterator();
		while (it.hasNext()) {
			ReportData dt = it.next();
			records.add(new String[] { dt.getTestCase(), dt.getStatus(), dt.getCache(), dt.getKeyValue(), dt.getExtraInfo() });
		}
		return records;
	}
}
