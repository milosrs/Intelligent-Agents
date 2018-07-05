package beans;

import java.util.ArrayList;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.inject.Inject;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;

@Stateful
@Remote(AgentInterface.class)
public class PongAgent extends AgentClass {

	private JMSTopic jmsTopic;
	private AgentsService agentsService;
	
	private static final long serialVersionUID = 1L;
	
	private AID aid;

	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.REQUEST)) {
			AID sender = message.getSender();
			if(sender.getType().getName().equals("PingAgent")) {
				ACLMessage aclMessage = new ACLMessage();
				aclMessage.setConversationId(message.getConversationId());
				aclMessage.setPerformative(Performative.INFORM);
				aclMessage.setSender(this.aid);
				ArrayList<AID> receivers = new ArrayList<AID>();
				receivers.add(sender);
				aclMessage.setReceivers(receivers);
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
