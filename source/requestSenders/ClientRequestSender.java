package requestSenders;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import beans.AID;

@Stateless
public class ClientRequestSender {

	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app";
	
	public void postRunningAgents(ArrayList<AID> agents, String hostAddress) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + hostAddress + NODE_URL + "/agents/running");
		webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(agents, MediaType.APPLICATION_JSON));
	}
}
