package beans;


import java.util.ArrayList;
import java.util.Random;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.inject.Inject;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;

@Stateful
@Remote(AgentInterface.class)
public class ParticipantAgent extends AgentClass {

	private static final long serialVersionUID = 1L;
	
	private AID aid;
	
	private JMSTopic jmsTopic;
	
	private AgentsService agentsService;
	
	@Override
	public void handleMessage(ACLMessage message) {
		
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setSender(this.aid);
		aclMessage.setConversationId(message.getConversationId());
		ArrayList<AID> receivers = new ArrayList<AID>();
		receivers.add(message.getSender());
		
		aclMessage.setReceivers(receivers);
		
		if(message.getPerformative().equals(Performative.CALL_FOR_PROPOSAL)) {
			
			Random random = new Random();
			int rand = random.nextInt(1);
			
			if(rand==0) {
				aclMessage.setPerformative(Performative.REFUSE);
			}else if(rand==1) {
				aclMessage.setPerformative(Performative.PROPOSE);
			}
			
			sendSocketMessage(aclMessage);		
			jmsTopic.send(aclMessage);
			
		}else if(message.getPerformative().equals(Performative.ACCEPT_PROPOSAL)) {
			
			Random random = new Random();
			int rand = random.nextInt(1);
			
			if(rand==0) {
				aclMessage.setPerformative(Performative.FAILURE);
				sendSocketMessage(aclMessage);		
				jmsTopic.send(aclMessage);
			}else if(rand==1) {
				
				aclMessage.setPerformative(Performative.INFORM);
				sendSocketMessage(aclMessage);		
				jmsTopic.send(aclMessage);
				
				aclMessage.setPerformative(Performative.INFORM);
				aclMessage.setContentObj("GOTOVO!");
				sendSocketMessage(aclMessage);		
				jmsTopic.send(aclMessage);
				
			}
		}
		
	}

	@Override
	public void setAid(AID aid) {
		this.aid = aid;
	}

	@Override
	public AID getAid() {
		return this.aid;
	}
	
	@Override
	public void init(JMSTopic topic, AgentsService agentService) {
		this.jmsTopic = topic;
		this.agentsService = agentService;
	}
	
}
