package com.tracesafe.subscriber.sanity.checker.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

	public static Object getObjectFromFile(String jsonFilePath, Class<?> classObject) {
		try {
			return new ObjectMapper().readValue(new File(jsonFilePath), classObject);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
			return null;
		}
	}

	public static String getJson(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			LOGGER.error("JsonProcessingException", e);
			return null;
		}
	}
}
