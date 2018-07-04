package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import interfaces.AgentInterface;
import services.AgentsService;
import services.HandshakeService;

@Path("/handshake")
public class HandshakeController {

	@Inject
	private HandshakeService handshakeService;
	
	@Inject
	private AgentsService agentsService;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node")
	public Response registerSlaveNode(Host newSlave) {
		List<Host> slavesList = handshakeService.startHandshake(newSlave);
		
		return Response.ok(slavesList).build();
	}
	
	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<AgentTypeDTO> getAllAgentTypes(){
		
		ArrayList<AgentTypeDTO> retList = new ArrayList<AgentTypeDTO>();
		
		for (Iterator<AgentTypeDTO> i = agentsService.getAllSupportedAgentTypes().iterator(); i.hasNext();)
		    retList.add(i.next());
		
		return retList;
	}
	
	@POST
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postRunningAgents(ArrayList<AID> agentsList){
		Response resp = null;
		
		try {
			for (Iterator<AID> i = agentsList.iterator(); i.hasNext();)
				agentsService.getAllRunningAgents().add(i.next());	
			resp = Response.ok(true).build();
		} catch(Exception e) {
			resp = Response.serverError().entity(false).build();
		}
		
		return resp;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/classes")
	public Response registerNewAgentClasses(List<AgentTypeDTO> agentTypes) {
		boolean success = handshakeService.addNewAgentTypes(agentTypes);
		
		return Response.ok(success).build();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@DELETE
	@Path("/node/{alias}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteNode(@PathParam(value = "alias") String alias, ArrayList<AgentType> agentsToDelete){
		
		boolean retVal = true;
				
		boolean hasDeleted = agentsService.getSlaveNodes().removeIf(x -> x.getAlias().equals(alias));
		boolean hasRemoved = true;
		if(!agentsToDelete.isEmpty())
			hasRemoved = agentsService.getAllSupportedAgentTypes().remove(agentsToDelete);
			
		if(!hasDeleted || !hasRemoved)
			retVal = false;
			
		return retVal;
	}
	
	
}
