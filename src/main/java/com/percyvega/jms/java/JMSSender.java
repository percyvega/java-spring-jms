package com.percyvega.jms.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class JMSSender {
    private static final Logger logger = LoggerFactory.getLogger(JMSSender.class);

    private static InitialContext initialContext;
    private static QueueConnectionFactory queueConnectionFactory;
    private static QueueConnection queueConnection;
    private static QueueSession queueSession;
    private static Queue queue;
    private static QueueSender qsndr;
    private static TextMessage textMessage;

    private static final String QCF_NAME = "XAConnectionFactory";   // Queue Connection Factory name
    private static final String QUEUE_NAME = "percyvegaJmsQueue";   // Queue name

    public static void sendMessage(String messageText) {
        logger.debug("Starting sendMessage(" + messageText + ")");

        try {

            Hashtable properties = new Hashtable();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");   // initial context
            properties.put(Context.PROVIDER_URL, "t3://dp-gsm3:7001");  // server:port
//            properties.put(Context.SECURITY_PRINCIPAL, "username");       // username
//            properties.put(Context.SECURITY_CREDENTIALS, "password");   // password

            initialContext = new InitialContext(properties);
            logger.debug("Got InitialContext " + initialContext.toString());

            // create QueueConnectionFactory
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(QCF_NAME);
            logger.debug("Got QueueConnectionFactory " + queueConnectionFactory.toString());

            // create QueueConnection
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.debug("Got QueueConnection " + queueConnection.toString());

            // create QueueSession
            queueSession = queueConnection.createQueueSession(false, 0);
            logger.debug("Got QueueSession " + queueSession.toString());

            // lookup Queue
            queue = (Queue) initialContext.lookup(QUEUE_NAME);
            logger.debug("Got Queue " + queue.toString());

            // create QueueSender
            qsndr = queueSession.createSender(queue);
            logger.debug("Got QueueSender " + qsndr.toString());

            // create TextMessage
            textMessage = queueSession.createTextMessage();
            logger.debug("Got TextMessage " + textMessage.toString());

            // set textMessage text in TextMessage
            textMessage.setText(messageText);
            logger.debug("Set text in TextMessage " + textMessage.toString());

            // send textMessage
            qsndr.send(textMessage);
            logger.debug("Sent textMessage ");

            // clean up
            qsndr.close();
            queueSession.close();
            queueConnection.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }

        logger.debug("Finishing sendMessage");
    }

    public static void main(String args[]) {
        sendMessage("test");
    }
}