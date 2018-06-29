package controllers;

import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
@Path("/heartbeat")
public class HeartBeatController {

	@GET
	@Path("/node")
	public void areYouAlive() {
		
	}
}
