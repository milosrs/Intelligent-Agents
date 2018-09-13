package jms;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.ACLMessage;
import beans.AID;
import beans.AgentClass;
import interfaces.AgentInterface;

import javax.ejb.MessageDriven;

import java.util.ArrayList;
import java.util.Iterator;

import javax.ejb.ActivationConfigProperty;

import services.AgentsService;

@MessageDriven(name = "MDBConsumer", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/agentTopic"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MDBConsumer implements MessageListener{

	@Inject
	private AgentsService agentsService;
	
	@Override
	public void onMessage(Message msg) {
		try {
			processMessage(msg);
		} catch (JMSException e) {
			e.printStackTrace();
			//WEBSOCKET MESSAGE?
			
		} catch (InstantiationException e) {
			e.printStackTrace();
			//WEBSOCKET MESSAGE?
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//WEBSOCKET MESSAGE?
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			//WEBSOCKET MESSAGE?
			
		}
	}

	private void processMessage(Message msg) throws JMSException, InstantiationException,
	IllegalAccessException, ClassNotFoundException {
		ACLMessage acl = (ACLMessage) ((ObjectMessage) msg).getObject();
		ArrayList<AID> receiversList = acl.getReceivers();
		for(Iterator<AID> i = receiversList.iterator(); i.hasNext();)
			deliverMessage(acl, i.next());
	}

	/*private AID getAid(Message msg, ACLMessage acl) throws JMSException {
		int i = msg.getIntProperty("AIDIndex");
		return acl.receivers.get(i);
	}*/

	private void deliverMessage(ACLMessage msg, AID aid) throws InstantiationException,
	IllegalAccessException, ClassNotFoundException {
		
		boolean agentExists = false;
		for (AgentInterface agent : agentsService.getMyRunningAgents()) {
			if(agent.getClass().isInstance(Class.forName("beans." + aid.getType().getName()).newInstance())) {
				AgentClass agentObj = (AgentClass) Class.forName("beans." + aid.getType().getName()).cast(agent);
				AID myAid = agentObj.getAid();
				if(myAid.getHost().getAlias().equals(aid.getHost().getAlias())
						&& myAid.getHost().getHostAddress().equals(aid.getHost().getHostAddress())
							&& myAid.getName().equals(aid.getName())) {
					agent.handleMessage(msg);
					agentExists = true;
					break;
				}
			}
		}
		
		if(!agentExists) {
		}		
	}
}
