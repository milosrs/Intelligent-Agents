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
public class InitiatorAgent extends AgentClass{

private static final long serialVersionUID = 1L;
	
	private AID aid;
	
	private AgentsService agentsService;
	
	private JMSTopic jmsTopic;
	
	@Override
	public void handleMessage(ACLMessage message) {
		
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setSender(this.aid);
		aclMessage.setConversationId(message.getConversationId());
		
		if(message.getPerformative().equals(Performative.REQUEST)) {
			
			aclMessage.setPerformative(Performative.CALL_FOR_PROPOSAL);
			
			ArrayList<AID> participants = new ArrayList<AID>();
			
			for(AID participant : agentsService.getAllRunningAgents()) {
				if(participant.getType().getName().equals("ParticipantAgent")) {
					participants.add(participant);
				}
			}
			
			aclMessage.setReceivers(participants);
			sendSocketMessage(aclMessage);
			jmsTopic.send(aclMessage);
		}else if(message.getPerformative().equals(Performative.PROPOSE)) {
			
			Random random = new Random();
			int rand = random.nextInt(1);
			
			if(rand==0) {
				aclMessage.setPerformative(Performative.REJECT_PROPOSAL);
			}else if(rand==1) {
				aclMessage.setPerformative(Performative.ACCEPT_PROPOSAL);
			}
			
			ArrayList<AID> receivers = new ArrayList<AID>();
			receivers.add(message.getSender());
			aclMessage.setReceivers(receivers);
			sendSocketMessage(aclMessage);
			jmsTopic.send(aclMessage);
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
