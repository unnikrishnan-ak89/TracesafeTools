package com.tracesafe.subscriber.sanity.checker.config;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttConnect implements MqttCallbackExtended {

	MemoryPersistence persistence = new MemoryPersistence();
	String server = null;
	String mClientId = null;
	String mUserName = null;
	String mUserPassword = null;

	public MqttConnect(String hostname, String client, String username, String password) {
		this.server = hostname;
		this.mClientId = client;
		this.mUserName = username;
		this.mUserPassword = password;
	}

	public void connectionLost(Throwable arg0) {
		LOGGER.info("connection lost client : {}",this.mClientId);
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	public void connectComplete(boolean reconnect, String message) {
	
	}

	public void messageArrived(String topic, MqttMessage message) {

	}

	MqttAsyncClient client = null;

	public MqttAsyncClient register() {
		try {
			if (client == null) {
				client = new MqttAsyncClient(server, mClientId, persistence);
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setUserName(mUserName);
				connOpts.setPassword(mUserPassword.toCharArray());
				connOpts.setConnectionTimeout(0);
				connOpts.setKeepAliveInterval((60 * 3) - 1);
				connOpts.setAutomaticReconnect(true);

				connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
				connOpts.setCleanSession(false);
				client.setCallback(this);
				client.connect(connOpts);

			}

		} catch (Exception me) {
			LOGGER.error("Exception on MqttAsyncClient register", me);
			if (me instanceof MqttException) {
				LOGGER.error("mqtt connection exception reason {}", ((MqttException) me).getReasonCode(), me);
			}
		}
		return client;
	}

}
