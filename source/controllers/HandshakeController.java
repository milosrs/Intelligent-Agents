package controllers;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import services.GetHostDataService;
import services.RestHandshakeService;

@Singleton
@Path("/handshake")
public class HandshakeController {

	@Inject
	private RestHandshakeService handshakeService;
	
//	@Inject
//	private GetHostDataService hostDataService;
	
	@POST
	@Path("/node")
	public void registerSlaveNode() {
		
	}
	
	@GET
	@Path("/agents/classes")
	public void getSupportedAgentClasses() {
		
	}
	
	@POST
	@Path("/agents/classes")
	public void registerNewAgentClasses() {
		
	}
	
	//Ovo mora da je put ili bilo sta drugo, ne moze post, a i nema smisla za istu putanju
	@PUT
	@Path("/node")
	public void registerNewSlaveNode() {
		
	}
	
	@POST
	@Path("/node")
	public void createSlaveNodesList() {
		
	}
	
	@POST
	@Path("/agents/sendSupportedAgents")		//Mora putanja da se promeni, opet nema smisla, jedino da gadjamo drugi kontroler, ali zasto bi :D
	public void sendSupportedAgentsList() {
		
	}
	
	@POST
	@Path("/agents/running")
	public void sendRunningAgentsList() {
		
	}
	
	@DELETE
	@Path("/node/{alias}")
	public void rollback(@PathParam("alias") String alias) {
		
	}
	
	
}
