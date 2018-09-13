package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.ACLMessage;
import beans.AID;
import beans.AgentClass;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.Message;
import beans.enums.NodeType;
import beans.enums.Performative;
import factories.AgentsFactory;
import interfaces.AgentInterface;
import jms.JMSTopic;
import requestSenders.ClientRequestSender;
import requestSenders.HandshakeRequestSender;
import services.AgentsService;

@Path("/app")
public class RestController {

	@Inject
	private AgentsService agentsService;
	
	@Inject
	private ClientRequestSender clientRequestSender;
	
	@Inject
	private HandshakeRequestSender requestSender;
	
	@Inject
	private JMSTopic jmsTopic;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@GET
	@Path("/node")
	@Produces(MediaType.TEXT_PLAIN)
	public String node() {
		
		return "ACTIVE";
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getRunningAgents(){
		
		return agentsService.getAllRunningAgents();
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String putNewAgent(@PathParam(value = "type") String type, @PathParam(value = "name") String name) throws JsonProcessingException, IOException{
		String retStr = "";		
		Host myHostData = agentsService.getMyHostInfo();
		boolean shouldAddAgent = agentsService.getAllRunningAgents()
											  .stream().filter( aid -> 
											  					aid.getName().equals(name) && 
											  					aid.getHost().getHostAddress().equals(myHostData.getHostAddress()) && 
											  					aid.getHost().getAlias().equals(myHostData.getAlias())
											   ).findFirst() != null;
		
		if(!shouldAddAgent) {
			retStr = "Agent with the given name already exists on this node!";
		} else {
			boolean isMainNode = agentsService.getMainNode().getHostAddress().equals(myHostData.getHostAddress())
								 && agentsService.getMainNode().getAlias().equals(myHostData.getAlias());
			
			for (AgentTypeDTO item : agentsService.getAllSupportedAgentTypes()) {
				if(item.getName().equals(type)) {
					//add agent to my list
					AID aid = new AID(name, myHostData, new AgentType(item.getName(), item.getModule()));
					AgentInterface myAgent = AgentsFactory.createAgent(aid,jmsTopic,agentsService);
					agentsService.getMyRunningAgents().add(myAgent);
					agentsService.getAllRunningAgents().add(aid);
					
					//initialize WEB-SOCKET
					Iterator<Session> iterator = WebSocketController.sessions.iterator();
					while(iterator.hasNext()) {
						Session s = iterator.next();
						s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("startAgent", mapper.writeValueAsString(aid))));
					}
					
					for(Host h : agentsService.getSlaveNodes())
						clientRequestSender.postNewRunningAgent(aid, h.getHostAddress());
					
					if(!isMainNode)
						clientRequestSender.postNewRunningAgent(aid, agentsService.getMainNode().getHostAddress());
					
					retStr = "SUCCESS";
					break;
				}
			}	
		}
		
		return retStr;
	}
	
	@DELETE
	@Path("/agents/running/{aid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRunningAgent(@PathParam(value = "aid") String aid) throws ParseException, 
	InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		
		String retStr = "";
		
		String[] splits = aid.split("__");
		String agentName = splits[0];
		String hostAddress = splits[1];
		String alias = splits[2];
		String agentType = splits[3];
		
		AID queryAid = agentsService.getAllRunningAgents().stream()
				.filter(o -> o.getHost().getHostAddress().equals(hostAddress)
					&& o.getHost().getAlias().equals(alias)
						&& o.getName().equals(agentName))
							.findFirst().orElse(null);			
		
		if(queryAid == null)
			retStr = "Can't find the agent by given AID.";		
		else{
			agentsService.getAllRunningAgents().remove(queryAid);
			
			//WEBSOCKET GOES HERE
			
			Iterator<Session> iterator = WebSocketController.sessions.iterator();
			while(iterator.hasNext()) {
				Session s = iterator.next();
				s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("stopAgent", mapper.writeValueAsString(queryAid))));
			}
			
			//check if this is my Agent and delete from that list
			boolean isMyAgent = false;
			if(agentsService.getMyHostInfo().getHostAddress().equals(hostAddress)
					&& agentsService.getMyHostInfo().getAlias().equals(alias))
					isMyAgent = true;
			
			if(isMyAgent) {
				for (Iterator<AgentInterface> i = agentsService.getMyRunningAgents().iterator(); i.hasNext();) {
					AgentInterface item = i.next();
					if(item.getClass().isInstance(Class.forName("beans." + agentType).newInstance())) {
						AgentClass agentObj = (AgentClass) Class.forName("beans." + agentType).cast(item);
						AID myAid = agentObj.getAid();
						if(myAid.getName().equals(agentName)) {
							agentsService.getMyRunningAgents().remove(item);
							break;
						}
					}
				}
			}
			
			boolean isMainNode = false;
			if(agentsService.getNodeType().equals(NodeType.MASTER))
				isMainNode = true;
			
			//send delete request to all (other) slaves
			for(Iterator<Host> i = agentsService.getSlaveNodes().iterator(); i.hasNext();) {
				String resp = clientRequestSender.deleteRunningAgents(i.next(), queryAid);
				System.out.println(resp);
			}
			
			//if I am a slave, send to the main node also
			if(!isMainNode) {
				String resp = clientRequestSender.deleteRunningAgents(agentsService.getMainNode(), queryAid);
				System.out.println(resp);
			}
			
			retStr = aid;
		}
		
		return retStr;
	}
	
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMessage(ACLMessage aclMessage) throws JsonProcessingException, IOException{
		
		Iterator<Session> iterator = WebSocketController.sessions.iterator();
		while(iterator.hasNext()) {
			Session s = iterator.next();
			aclMessage.setContent(aclMessage.getContent());
			s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("aclMessage", mapper.writeValueAsString(aclMessage))));
		}
		
		jmsTopic.send(aclMessage);
		
		return "Success";
	}
	
	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Performative[] getMessages() {
		return Performative.values();
	}
	
	@POST
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postRunningAgents(AID aid){
		boolean success = true;
		
		try {
			
			if (!agentsService.getAllRunningAgents().contains(aid)) {
				agentsService.getAllRunningAgents().add(aid);
			}
			
			Iterator<Session> iterator = WebSocketController.sessions.iterator();
			while(iterator.hasNext()) {
				Session s = iterator.next();
				s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("startAgent", mapper.writeValueAsString(aid))));
			}
		} catch(Exception e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}
}
