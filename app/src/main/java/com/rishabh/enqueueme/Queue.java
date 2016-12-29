package com.rishabh.enqueueme;

/**
 * Created by lenovo on 27/12/2016.
 */
public class Queue {
    private String queueName;
    private String yourQueueNumber;
    private String currentNumber;
    private String deviceId;

    public Queue(String queueName, String yourQueueNumber, String currentNumber, String deviceId) {
        this.queueName = queueName;
        this.yourQueueNumber = yourQueueNumber;
        this.currentNumber = currentNumber;
        this.deviceId = deviceId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getYourQueueNumber() {
        return yourQueueNumber;
    }

    public void setYourQueueNumber(String yourQueueNumber) {
        this.yourQueueNumber = yourQueueNumber;
    }

    public String getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(String currentNumber) {
        this.currentNumber = currentNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
