package services;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Host;
import interfaces.AgentInterface;

@Stateless
public class RestHandshakeService {

	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app";

	public Response getRunningAgents(String hostAddress) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + hostAddress + NODE_URL + "/agents/running");
		return webTarget.request(MediaType.APPLICATION_JSON).get();
	}
	
	public Response deleteAgents(Host host, ArrayList<AgentInterface> agentsToDelete) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + host.getHostAddress() + NODE_URL + "/node/{" + host.getAlias() + "}");
		return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(agentsToDelete, MediaType.APPLICATION_JSON));
	}
}
