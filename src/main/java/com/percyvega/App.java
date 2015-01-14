package com.percyvega;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");

        InvoiceQueueSender sender = (InvoiceQueueSender) context.getBean("jmsInvoiceSender");

        for (int i = 0; ; i++) {
            sender.sendMesage("This is my JMS message #" + i + "!");
        }

    }
}
