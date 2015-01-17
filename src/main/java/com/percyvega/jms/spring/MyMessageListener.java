package com.percyvega.jms.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MyMessageListener.class);

    public void onMessage(Message message) {
        try {
            logger.debug("Received message: " + ((TextMessage) message).getText());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }
    
}