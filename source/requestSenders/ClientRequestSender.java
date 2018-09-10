package requestSenders;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
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

@Singleton
public class ClientRequestSender {

	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app/agents/running";
	
	@PostConstruct
	public void init() {
		restClient = ClientBuilder.newClient();
	}
	
	public void postNewRunningAgent(AID aid, String hostAddress) {
		webTarget = restClient.target(HTTP_URL + hostAddress + NODE_URL);
		webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(aid, MediaType.APPLICATION_JSON));
	}
	
	public String deleteRunningAgents(Host host, AID aid) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();		
		String aidStr = mapper.writeValueAsString(aid);
		webTarget = restClient.target(HTTP_URL + host.getHostAddress() + NODE_URL + "/{aid}");
		Response resp = webTarget.resolveTemplate("aid", aidStr).request().delete();
		return resp.readEntity(String.class);
	}
}
