package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.websocket.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.enums.Performative;
import controllers.WebSocketController;
import interfaces.AgentInterface;
import services.AgentsService;
import services.ReduceService;
import services.appConfigServices.JMSTopic;

@Stateful
@Remote(AgentInterface.class)
public class ReduceAgent extends AgentClass{
	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic jmsTopic;
	private AgentsService agentsService;
	private ReduceService reduceService;
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.PROPAGATE)
			&& message.getSender().getType().getName().equals("MapAgent")) {
			boolean shouldReset;
			System.out.println(this.aid.getName() + " Invoked on : " + this.agentsService.getMyHostInfo().getHostAddress());
			
			if(reduceService == null) {
				reduceService = new ReduceService();
			}
			
			System.out.println("Reduce invoked on: " + agentsService.getMyHostInfo().getHostAddress() + " by: " + message.getSender().getName());
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
			
			shouldReset = reduceService.areAllMappersProcessed(getMappers(message));
			System.out.println("Should Reset: " + shouldReset);
			
			if(shouldReset) {
				
				String content = reduceService.createHugeString().trim();
				System.out.println(content);
				
				if(content != "" && content != null) {
					Message msg = new Message("aclMessage", "************RESPONSE********** " + content);
					
					Iterator<Session> iterator = WebSocketController.sessions.iterator();
					System.out.println(agentsService.getMyHostInfo().getHostAddress() + " is sending a response!");
					int i = 0;
					while(iterator.hasNext()) {
						System.out.println(agentsService.getMyHostInfo().getHostAddress() + " iterator: " + i++);
						Session s = iterator.next();
						try {
							s.getBasicRemote().sendText(mapper.writeValueAsString(msg));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					reduceService.resetAll();
				}
			}
		}
	}
	
	private List<AID> getMappers(ACLMessage message) {
		List<AID> mappers = new ArrayList<AID>();
		
		for(AID mapper : message.getReceivers()) {
			if(mapper.getType().getName().equals("MapAgent")) {
				mappers.add(mapper);
			}
		}
		
		return mappers;
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
