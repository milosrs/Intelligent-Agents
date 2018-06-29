package controllers;

import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import beans.ACLMessage;

@Singleton
@Path("/agents")
public class ClientCommunicationController {

	@GET
	@Path("/classes")
	public void getAllAgentClasses() {
		
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
