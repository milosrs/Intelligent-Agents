package beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.inject.Inject;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import mapreduceBean.MapReduceDetails;
import services.AgentsService;
import services.MapService;

@Stateful
@Remote(AgentInterface.class)
public class MapAgent extends AgentClass{
	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic jmsTopic;
	private AgentsService agentsService;
	private MapService mapService;
	private int mapNumber;
	private HashMap<String, Integer> counts;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.CALL_FOR_PROPOSAL)
			&& countMappers(message.getReceivers())) {
			try {
				
				int positionInArray = positionInList(message.getReceivers());
				if(positionInArray > -1) {
					System.out.println("Turning on Mapper: " + this.aid.getName());
					mapService = new MapService();
					mapService.init();
					mapService.createKeyValuePairs(message.getContent(), positionInArray, mapNumber);
					
					ACLMessage reducerMsg = new ACLMessage();
					reducerMsg.setConversationId(message.getConversationId());
					reducerMsg.setPerformative(Performative.PROPAGATE);
					reducerMsg.setReceivers(message.getReceivers());
					reducerMsg.setSender(message.getSender());
					reducerMsg.setReplyTo(message.getSender());
	
					HashMap<String, Object> userArgs = createMapReduceDetails(mapService.getCounts(), message);
					
					reducerMsg.setUserArgs(userArgs);
					
					jmsTopic.send(reducerMsg);
					mapNumber = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int positionInList(ArrayList<AID> receivers) {
		int ret = 0;
		
		for(int i = 0; i < receivers.size(); i++) {
			if(receivers.get(i).getName().equals(this.aid.getName())) {
				break;
			}
			
			ret++;
		}
		
		return ret;
	}

	private HashMap<String, Object> createMapReduceDetails(HashMap<String, Integer> details,  ACLMessage message) {
		HashMap<String, Object> ret = new HashMap<String, Object>();

		for(int i = 0; i < message.getReceivers().size(); i++) {
			if(message.getReceivers().get(i).getType().getName().equals("ReduceAgent")) {
				ret.put(message.getReceivers().get(i).getName(), details);
			}		
		}
		
		return ret;
	}

	private boolean countMappers(ArrayList<AID> receivers) {
		for(AID aid : receivers) {
			if(aid.getType().getName().equals("MapAgent")) {
				mapNumber++;
			}
		}
		
		return mapNumber > 1;
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
