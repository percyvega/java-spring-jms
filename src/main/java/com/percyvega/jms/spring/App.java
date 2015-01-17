package com.percyvega.jms.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.debug("Starting main(...)");

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        MessageSender sender = (MessageSender) context.getBean("myMessageSender");

        for (int i = 0; i < 100; i++) {
            sender.sendMessage("This is my JMS message #" + i + "!");
        }

        logger.debug("Finishing main(...)");
    }
}
