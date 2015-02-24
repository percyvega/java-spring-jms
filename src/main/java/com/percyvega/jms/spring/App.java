package com.percyvega.jms.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    ApplicationContext context;
    private MessageSender sender;

    public App() {
        context = new ClassPathXmlApplicationContext("spring-config.xml");
        sender = (MessageSender) context.getBean("myMessageSender");

        new Thread(new Producer()).start();
    }

    public static void main(String[] args) throws InterruptedException {
        logger.debug("Starting main(...)");

        new App();

        logger.debug("Finishing main(...)");
    }

    class Producer implements Runnable {

        private Random random = new Random(System.currentTimeMillis());

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sender.sendMessage("This is my JMS message #" + i + "!");
            }
        }
    }

}
