package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.Message;
import factories.AgentsFactory;
import interfaces.AgentInterface;
import requestSenders.ClientRequestSender;
import requestSenders.HandshakeRequestSender;
import services.AgentsService;
import services.HandshakeService;

@Path("/app")
public class RestController {

	@Inject
	private AgentsService agentsService;
	
	@Inject
	private HandshakeService restHandshakeService;
	
	@Inject
	private HandshakeRequestSender requestSender;
	
	@Inject
	private ClientRequestSender clientRequestSender;
	
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
	public ArrayList<AID> getRunningAgents(){
		
		ArrayList<AID> retList = new ArrayList<AID>();
		
		for (Iterator<AID> i = agentsService.getAllRunningAgents().iterator(); i.hasNext();)
		    retList.add(i.next());
		
		return retList;
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String putNewAgent(@PathParam(value = "type") String type, @PathParam(value = "name") String name) throws JsonProcessingException, IOException{
		
		String retStr = "";
		
		boolean flag = true;		
		Host myHostData = agentsService.getMyHostInfo();
		if(agentsService.getAllRunningAgents().stream()
				.filter(o -> o.getName().equals(name) 
					&& o.getHost().getHostAddress().equals(myHostData.getHostAddress())
						&& o.getHost().getAlias().equals(myHostData.getAlias()))
							.findFirst().isPresent()) {
			retStr = "Agent with the given name already exists on this node!";
			flag = false;
		}
		
		if(flag) {
			
			boolean isMainNode = false;
			if(agentsService.getMainNode().getHostAddress().equals(myHostData.getHostAddress())
					&& agentsService.getMainNode().getAlias().equals(myHostData.getAlias()))
				isMainNode = true;
			
			for (Iterator<AgentTypeDTO> i = agentsService.getAllSupportedAgentTypes().iterator(); i.hasNext();) {
				AgentTypeDTO item = i.next();
				if(item.getName().equals(type)) {
					//add agent to my list
					AID aid = new AID(name, myHostData, new AgentType(item.getName(), item.getModule()));
					AgentInterface myAgent = AgentsFactory.createAgent(aid);
					agentsService.getMyRunningAgents().add(myAgent);
					agentsService.getAllRunningAgents().add(aid);
					
					//initialize WEB-SOCKET
					Iterator<Session> iterator = WebSocketController.sessions.iterator();
					while(iterator.hasNext()) {
						Session s = iterator.next();
						s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("startAgent", mapper.writeValueAsString(aid))));
					}
					System.out.println("gasg");
					
					ArrayList<AID> postList = new ArrayList<AID>();
					postList.add(aid);
					//send AID to all other nodes
					for(Iterator<Host> h = agentsService.getSlaveNodes().iterator(); h.hasNext();)
						clientRequestSender.postRunningAgents(postList, h.next().getHostAddress());
					
					//if I am a slave node, send AID to the main node also
					if(!isMainNode)
						clientRequestSender.postRunningAgents(postList, agentsService.getMainNode().getHostAddress());
					
					retStr = "SUCCESS";
					break;
				}
			}	
		}
		
		return retStr;
	}
	
	/*@POST
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<AgentInterface> postRunningAgents(Host senderHostData){
		
		ArrayList<AID> retList = new ArrayList<AID>();
		Host myHostData = agentsService.getMyHostInfo();
		
		//add my running agents
		for (Iterator<AID> i = agentsService.getAllRunningAgents().iterator(); i.hasNext();) {
			AID item = i.next();
			if(item.getHost().getAlias().equals(myHostData.getAlias())
					&& item.getHost().getHostAddress().equals(myHostData.getHostAddress())) {
			    retList.add(item);
			}
		}

		//only sending to other slaves (if I am the main node I will skip the slave who initiated the call)
		for (Iterator<Host> h = agentsService.getSlaveNodes().iterator(); h.hasNext();) {
			Host item = h.next();
			if(!item.getHostAddress().equals(myHostAddress)) {
				Response resp = requestSender.getRunningAgents(item.getHostAddress());
				ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
				for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
					retList.add(ra.next());
			}
		}
		
		//i am a slave node, send also to the main node, FIX THE MEEEE
		if(!agentsService.getMainNode().getHostAddress().equals("ME")) {
			Host main = agentsService.getMainNode();
			Response resp = requestSender.getRunningAgents(main.getHostAddress());
			ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
			for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
				retList.add(ra.next());
		}
		
		return retList;
	}*/
}
