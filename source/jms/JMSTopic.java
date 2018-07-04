package jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/***
 * 
 * @author Riki
 * Kako koristiti klasu:
 * 0. Podesiti da server koristi standalone-full-ha ili standalone-full
 * 1. Napraviti preko add-user novog Application Usera (kod mene je to un: guest, pw: guest)
 * 2. Otici na localhost:999x (admin console) i podesiti JMS preko Configuration: Subsystems -> Subsystem: Messaging - ActiveMQ -> Settings: Messaging Provider -> Messaging Provider: default
 * 4. Nazvati vas topic agentTopic, za jndi ime mu dati java:jboss/agentTopic
 * 5. Kupiti Rikiju cokoladicu
 */
@Stateful
public class JMSTopic implements MessageListener {

	private final String REMOTE_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
	private final String JMSTopic_NAME = "java:jboss/agentTopic";
	private final String USER = "guest";
	private Topic topic;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Connection connection;
	
	@PostConstruct
	public void construct() throws NamingException, JMSException {
		Context context = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory) context.lookup(REMOTE_CONNECTION_FACTORY);
		topic = (Topic) context.lookup(JMSTopic_NAME);
		context.close();
		
		connection = cf.createConnection(USER, USER);
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
		
		consumer = session.createConsumer(topic);
		consumer.setMessageListener(this);
		
		producer = session.createProducer(topic);
	}
	
	@PreDestroy
	public void closeAllConnections() throws JMSException {
		consumer.close();
		producer.close();
		connection.close();
	}
	
	@Override
	public void onMessage(Message msg) {
		System.out.println("Recieved a message!");
	}

}
