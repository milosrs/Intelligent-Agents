package beans;

import java.io.IOException;
import java.util.Iterator;

import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.WebSocketController;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;

public abstract class AgentClass implements AgentInterface {

	private static final long serialVersionUID = 1L;
	
	public abstract void setAid(AID aid);
	public abstract AID getAid();
	public abstract void init(JMSTopic topic, AgentsService agentService);

	protected void sendSocketMessage(ACLMessage message) {
		ObjectMapper mapper = new ObjectMapper();
		Iterator<Session> iterator = WebSocketController.sessions.iterator();
		while(iterator.hasNext()) {
			Session s = iterator.next();
			try {
				s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("aclMessage", mapper.writeValueAsString(message))));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
