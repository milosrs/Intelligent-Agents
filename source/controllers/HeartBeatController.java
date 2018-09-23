package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/heartbeat")
public class HeartBeatController {

	@GET
	@Path("/node")
	public Response areYouAlive() {
		return Response.ok().build();
	}
}
