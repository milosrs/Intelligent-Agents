package beans;

import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.core.GenericType;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;
import services.ReduceService;

@Stateful
@Remote(AgentInterface.class)
public class ReduceAgent extends AgentClass{
	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic jmsTopic;
	private AgentsService agentsService;
	private ReduceService reduceService;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.PROPAGATE)
			&& message.getSender().getType().getName().equals("MapAgent")) {
			boolean shouldReset;
			
			System.out.println("Reduce invoked on: " + agentsService.getMyHostInfo().getHostAddress());
			HashMap<String, Object> userArgs = message.getUserArgs();
			
			for(String key : userArgs.keySet()) {
				Object fromMap = userArgs.get(key);
				
				if(fromMap instanceof HashMap) {
					HashMap<String, Integer> mapperOutput = (HashMap<String, Integer>) fromMap;
					
					try {
						reduceService.countOccurences(mapperOutput, message.getSender());
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

			shouldReset = reduceService.areAllMappersProcessed(message.getReceivers());
			
			if(shouldReset) {
				reduceService.writeValues();
				reduceService.resetAll();
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
