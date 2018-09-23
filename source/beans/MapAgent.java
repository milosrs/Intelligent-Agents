package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import beans.enums.Performative;
import interfaces.AgentInterface;
import services.AgentsService;
import services.MapService;
import services.appConfigServices.JMSTopic;

@Stateful
@Remote(AgentInterface.class)
public class MapAgent extends AgentClass{
	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic jmsTopic;
	@SuppressWarnings("unused")
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
					reducerMsg.setSender(this.aid);
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
			} else if(receivers.get(i).getType().getName().equals("MapAgent")) {
				ret++;
			}
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
		
		return mapNumber > 0;
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

	public HashMap<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(HashMap<String, Integer> counts) {
		this.counts = counts;
	}

}
