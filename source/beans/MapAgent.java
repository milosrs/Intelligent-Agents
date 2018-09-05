package beans;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;

@Stateful
@Remote(AgentInterface.class)
public class MapAgent extends AgentClass{

	private static final long serialVersionUID = 1L;
	
	private AID aid;
	
	private JMSTopic jmsTopic;
	
	private AgentsService agentsService;
	
	@Override
	public void handleMessage(ACLMessage message) {
		
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
