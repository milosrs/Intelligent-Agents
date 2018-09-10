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
	private int reducerNumber;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.CALL_FOR_PROPOSAL)
			&& areAllReceiversReducers(message.getReceivers())) {
			try {
				mapService = new MapService();
				mapService.init();
				mapService.createKeyValuePairs(message.getContent());
				System.out.println("Turning on Reducers!");
				ACLMessage reducerMsg = new ACLMessage();
				reducerMsg.setConversationId(message.getConversationId());
				reducerMsg.setPerformative(Performative.PROPAGATE);
				reducerMsg.setReceivers(message.getReceivers());
				reducerMsg.setSender(message.getSender());
				reducerMsg.setReplyTo(message.getSender());
				
				int lineNumberPerReducer = Math.floorDiv(mapService.getNumberOfLines(), reducerNumber);
				HashMap<String, Object> userArgs = createMapReduceDetails(lineNumberPerReducer, message);
				
				jmsTopic.send(reducerMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private HashMap<String, Object> createMapReduceDetails(int lineNumberPerReducer, ACLMessage message) {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		int startAt = 0;
		
		for(int i = 0; i < reducerNumber; i++) {
			MapReduceDetails details = new MapReduceDetails(message.getContent(), startAt, startAt + lineNumberPerReducer);
			startAt += lineNumberPerReducer;
			ret.put(message.getReceivers().get(i).getName(), details);
		}
		
		return ret;
	}

	private boolean areAllReceiversReducers(ArrayList<AID> receivers) {
		boolean success = true;
		reducerNumber = 0;
		
		for(AID aid : receivers) {
			success = aid.getType().getName().equals("ReduceAgent")
					  || aid.getType().getName().equals("MapAgent");

			if(!success) {
				break;
			} else {
				if(aid.getType().getName().equals("ReduceAgent")) {
					reducerNumber++;
				}
			}
		}
		return success;
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
