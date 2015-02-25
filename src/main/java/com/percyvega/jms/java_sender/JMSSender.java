package com.percyvega.jms.java_sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

/**
 * Created by pevega on 1/21/2015.
 */
public class JMSSender {

    private static final Logger logger = LoggerFactory.getLogger(JMSSender.class);

    private InitialContext initialContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Queue queue;
    private QueueSender qsndr;
    private TextMessage textMessage;

    private static final String ICF_NAME = "weblogic.jndi.WLInitialContextFactory";     // Initial Context Factory name
    private static final String PROVIDER_URL = "t3://dp-devcarrier1:8001";              // Provider url (server:port)
    private static final String QCF_NAME = "jms/myConnectionFactory";                   // Connection Factory JNDI name
    private static final String QUEUE_NAME = "jms/percyvegaQueue";                      // Queue JNDI name

    @Override
    public String toString() {
        return "JMSSender [ICF_NAME=" + ICF_NAME + ", PROVIDER_URL=" + PROVIDER_URL + ", QCF_NAME=" + QCF_NAME + ", QUEUE_NAME=" + QUEUE_NAME + "]";
    }

    public JMSSender() {
        try {
            Hashtable properties = new Hashtable();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, ICF_NAME);
            properties.put(Context.PROVIDER_URL, PROVIDER_URL);
            //            properties.put(Context.SECURITY_PRINCIPAL, "username");                   // username
            //            properties.put(Context.SECURITY_CREDENTIALS, "password");                 // password

            initialContext = new InitialContext(properties);
//            logger.debug("Got InitialContext " + initialContext.toString());

            // create QueueConnectionFactory
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(QCF_NAME);
//            logger.debug("Got QueueConnectionFactory " + queueConnectionFactory.toString());

            // create QueueConnection
            queueConnection = queueConnectionFactory.createQueueConnection();
//            logger.debug("Got QueueConnection " + queueConnection.toString());

            // create QueueSession
            queueSession = queueConnection.createQueueSession(false, 0);
//            logger.debug("Got QueueSession " + queueSession.toString());

            // lookup Queue
            queue = (Queue) initialContext.lookup(QUEUE_NAME);
//            logger.debug("Got Queue " + queue.toString());

            // create QueueSender
            qsndr = queueSession.createSender(queue);
//            logger.debug("Got QueueSender " + qsndr.toString());

            // create TextMessage
            textMessage = queueSession.createTextMessage();
//            logger.debug("Got TextMessage " + textMessage.toString());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }

    public void sendMessage(String messageText) throws JMSException {
        // set textMessage text in TextMessage
        textMessage.setText(messageText);

        // send textMessage
        qsndr.send(textMessage);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        // clean up
        qsndr.close();
        queueSession.close();
        queueConnection.close();
    }

    public static void main(String args[]) throws JMSException {
        JMSSender jmsSender = new JMSSender();
        for (int i = 1; i <= 10; i++)
            jmsSender.sendMessage("This is my JMS message #" + i + "!");
    }
}