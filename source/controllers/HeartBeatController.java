package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/heartbeat")
public class HeartBeatController {

	@GET
	@Path("/node")
	public void areYouAlive() {
		
	}
}
