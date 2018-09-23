package services.appConfigServices;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.ACLMessage;

/***
 * 
 * @author Riki
 * Kako koristiti klasu:
 * 0. Podesiti da server koristi standalone-full-ha ili standalone-full
 * 1. Napraviti preko add-user novog Application Usera (kod mene je to un: guest, pw: guest, (@ credits by Timeeh) group: guest)
 * 2. Otici na localhost:999x (admin console) i podesiti JMS preko Configuration: Subsystems -> Subsystem: Messaging - ActiveMQ -> Settings: Messaging Provider -> Messaging Provider: default
 * 4. Nazvati vas topic agentTopic, za jndi ime mu dati java:jboss/agentTopic
 * 5. Kupiti Rikiju cokoladicu (i Timiju :)
 */
@Singleton
@LocalBean
public class JMSTopic implements MessageListener {

	private final String REMOTE_CONNECTION_FACTORY = "java:jboss/exported/jms/RemoteConnectionFactory";
	private final String JMSTopic_NAME = "java:jboss/agentTopic";
	private final String USER = "guest";
	private Topic topic;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Connection connection;
	private Session session;
	
	@PostConstruct
	public void construct() {
		try {
			Context context = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) context.lookup(REMOTE_CONNECTION_FACTORY);
			topic = (Topic) context.lookup(JMSTopic_NAME);
			context.close();
			
			connection = cf.createConnection(USER, USER);
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);
			
			producer = session.createProducer(topic);
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@PreDestroy
	public void closeAllConnections() {
		try {
			consumer.close();
			producer.close();
			connection.close();
			session.close();
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@Override
	public void onMessage(Message msg) {
	}

	public void send(ACLMessage msg) {	    
		ObjectMessage objMsg;	
		try {
			objMsg = this.session.createObjectMessage(msg);
			this.producer.send(objMsg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
