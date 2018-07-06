package requestSenders;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AID;
import beans.Host;

@Stateless
public class ClientRequestSender {

	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app";
	
	public void postRunningAgents(ArrayList<AID> agents, String hostAddress) {
		System.out.println("Ovo je ocigledno neka greska");
//		restClient = ClientBuilder.newClient();
//		webTarget = restClient.target(HTTP_URL + hostAddress + NODE_URL + "/agents/running");
//		webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(agents, MediaType.APPLICATION_JSON));
	}
	
	public String deleteRunningAgents(Host host, AID aid) throws JsonProcessingException {
		restClient = ClientBuilder.newClient();
		ObjectMapper mapper = new ObjectMapper();		
		String aidStr = mapper.writeValueAsString(aid);
		webTarget = restClient.target(HTTP_URL + host.getHostAddress() + NODE_URL + "/agents/running/{aid}");
		Response resp = webTarget.resolveTemplate("aid", aidStr).request().delete();
		return resp.readEntity(String.class);
	}
}
