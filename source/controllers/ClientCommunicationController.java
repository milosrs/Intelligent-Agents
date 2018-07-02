package controllers;

import java.util.List;

import javax.ejb.Singleton;
import javax.naming.NamingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.ACLMessage;
import beans.AgentType;
import services.JndiTreeParser;

@Singleton
@Path("/agents")
public class ClientCommunicationController {
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAvailableAgentClasses() {
		
		JndiTreeParser jndiTreeParser = new JndiTreeParser();
		
		  try {
		    return jndiTreeParser.parse();
		  } catch (NamingException ex) {
		    throw new IllegalStateException(ex);
		  }
	}
	
	@GET
	@Path("/running")
	public void getAllRunningAgents() {
		
	}
	
	@PUT
	@Path("/running/{type}/{name}")
	public void runAgent(@PathParam("type") String type, @PathParam("name") String name) {
		
	}
	
	@DELETE
	@Path("/running/{aid}")
	public void stopAgent(@PathParam("aid")String AID) {
		
	}
	
	@POST
	@Path("/messages")
	public void sendACLMessage(ACLMessage msg) {
		
	}
	
	@GET
	@Path("/messages")
	public void getPerfomativeList() {
		
	}
}
