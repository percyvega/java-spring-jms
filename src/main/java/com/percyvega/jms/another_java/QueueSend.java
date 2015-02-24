package com.percyvega.jms.another_java;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * Created by pevega on 1/21/2015.
 */
public class QueueSend {

    private static final String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";     // Initial Context Factory name
    private static final String PROVIDER_URL = "t3://dp-devcarrier1:8001";                         // Provider url (server:port)
    private static final String JMS_FACTORY = "jms/myConnectionFactory";                    // Connection Factory JNDI name
    private static final String QUEUE = "jms/percyvegaQueue";                                // Queue JNDI name

    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueSender qsender;
    private Queue queue;
    private TextMessage msg;

    private static InitialContext getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, PROVIDER_URL);
        return new InitialContext(env);
    }

    public void init(Context ctx, String queueName)
            throws NamingException, JMSException {
        qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
        qcon = qconFactory.createQueueConnection();
        qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = (Queue) ctx.lookup(queueName);
        qsender = qsession.createSender(queue);
        msg = qsession.createTextMessage();
        qcon.start();
    }

    public void send(String message) throws JMSException {
        msg.setText(message);
        qsender.send(msg);
    }

    private static void readAndSend(QueueSend qs) throws IOException, JMSException {
        String line = "Test Message Body with counter = ";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean readFlag = true;
        System.out.println("\n\tStart Sending Messages (Enter QUIT to Stop):\n");
        while (readFlag) {
            System.out.print("&lt;Msg_Sender&gt; ");
            String msg = br.readLine();
            if (msg.equals("QUIT") || msg.equals("quit")) {
                qs.send(msg);
                System.exit(0);
            }
            qs.send(msg);
            System.out.println();
        }
        br.close();
    }

    public void close() throws JMSException {
        qsender.close();
        qsession.close();
        qcon.close();
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Usage: java_sender QueueSend WebLogicURL");
//            return;
//        }
        InitialContext ic = getInitialContext();
        QueueSend qs = new QueueSend();
        qs.init(ic, QUEUE);
        readAndSend(qs);
        qs.close();
    }

}
