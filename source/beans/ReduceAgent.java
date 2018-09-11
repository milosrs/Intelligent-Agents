package beans;

import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;

@Stateful
@Remote(AgentInterface.class)
public class ReduceAgent extends AgentClass{
	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic jmsTopic;
	private AgentsService agentsService;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.PROPAGATE)
			&& message.getSender().getType().getName().equals("MapAgent")) {
			System.out.println("Reduce invoked on: " + agentsService.getMyHostInfo().getHostAddress());
			HashMap<String, Object> userArgs = message.getUserArgs();
			
			writeValues(userArgs);
		}
	}

	private void writeValues(HashMap<String, Object> userArgs) {
		for(String key : userArgs.keySet()) {
			HashMap<String, Integer> words = (HashMap<String, Integer>) userArgs.get(key);
			
			for(String word : words.keySet()) {
				System.out.println(word + ": " + words.get(word));
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
