package com.percyvega.jms.another_java;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Created by pevega on 1/21/2015.
 */
public class QueueReceive implements MessageListener {
    private static final String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";     // Initial Context Factory name
    private static final String PROVIDER_URL = "t3://dp-devcarrier1:8001";                         // Provider url (server:port)
    private static final String JMS_FACTORY = "jms/myConnectionFactory";                    // Connection Factory JNDI name
    private static final String QUEUE = "jms/percyvegaQueue";                                // Queue JNDI name

    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueReceiver qreceiver;
    private Queue queue;
    private boolean quit = false;

    public void onMessage(Message msg) {
        try {
            String msgText;
            if (msg instanceof TextMessage) {
                msgText = ((TextMessage) msg).getText();
            } else {
                msgText = msg.toString();
            }
            System.out.println("\n\t&lt;Msg_Receiver&gt; " + msgText);
            if (msgText.equalsIgnoreCase("quit")) {
                synchronized (this) {
                    quit = true;
                    this.notifyAll(); // Notify main thread to quit
                }
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    public void init(Context ctx, String queueName) throws NamingException, JMSException {
        qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
        qcon = qconFactory.createQueueConnection();
        qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = (Queue) ctx.lookup(queueName);
        qreceiver = qsession.createReceiver(queue);
        qreceiver.setMessageListener(this);
        qcon.start();
    }

    public void close() throws JMSException {
        qreceiver.close();
        qsession.close();
        qcon.close();
    }

    public static void main(String[] args) throws Exception {
        InitialContext ic = getInitialContext();
        QueueReceive qr = new QueueReceive();
        qr.init(ic, QUEUE);

        // Wait until a "quit" message has been received.
        synchronized (qr) {
            while (!qr.quit) {
                try {
                    qr.wait();
                } catch (InterruptedException ie) {
                }
            }
        }
        qr.close();
    }

    private static InitialContext getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, PROVIDER_URL);
        return new InitialContext(env);
    }
}
