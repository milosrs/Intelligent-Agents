package beans;


import javax.ejb.Remote;
import javax.ejb.Stateful;
import beans.enums.Performative;
import interfaces.AgentInterface;
import services.AgentsService;
import services.appConfigServices.JMSTopic;

@Stateful
@Remote(AgentInterface.class)
public class PingAgent extends AgentClass {
	
	private static final long serialVersionUID = 1L;
	private AgentsService agentsService;
	
	private JMSTopic jmsTopic;
	
	private AID aid;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.INFORM)) {
			sendSocketMessage(message);
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
