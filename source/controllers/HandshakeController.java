package controllers;

import java.util.ArrayList;
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

import beans.AgentType;
import beans.Host;
import interfaces.AgentInterface;
import services.RestHandshakeService;

@Path("/handshake")
public class HandshakeController {

	@Inject
	private RestHandshakeService handshakeService;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node")
	public Response registerSlaveNode(Host newSlave) {
		List<Host> slavesList = handshakeService.registerSlaveNode(newSlave);
		
		return Response.ok(slavesList).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/classes")
	public Response getSupportedAgentClasses() {
		return Response.ok(handshakeService.fetchAgentTypeList()).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/classes")
	public Response registerNewAgentClasses(List<AgentType> agentTypes) {
		boolean success = handshakeService.addNewAgentTypes(agentTypes);
		
		return Response.ok(success).build();
	}
	
	@POST
	@Path("/agents/sendSupportedAgents")		//Mora putanja da se promeni, opet nema smisla, jedino da gadjamo drugi kontroler, ali zasto bi :D
	public void sendSupportedAgentsList() {
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/agents/running")
	public Response postRunningAgents(String myHostAddress) {
		return Response.ok(handshakeService.sendRunningAgents(myHostAddress)).build();		
	}
	
	@DELETE
	@Path("/node/{alias}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rollback(@PathParam(value = "alias") String alias, ArrayList<AgentType> agentsToDelete) {
		return Response.ok(handshakeService.deleteNode(alias, agentsToDelete)).build();
	}
	
	
}
