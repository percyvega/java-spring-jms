package com.percyvega;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class InvoiceQueueSender {
    private JmsTemplate jmsTemplate;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMesage(final String s) {
        MessageCreator messageCreator = new MessageCreator() {
            public Message createMessage(Session session) throws
                    JMSException {
                return session.createTextMessage(s);
            }
        };

        jmsTemplate.send("jms/RI_Q", messageCreator);
    }
}