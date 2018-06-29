package services;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Stateless
public class RestHandshakeService {

	private Client restClient;
	private WebTarget webTarget;
	private static String NODEHANDLER_URL = "http://localhost:8080/NodeHandler/rest/app";
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app";
	private JSONParser parser = new JSONParser();
	
	public Response handleNode(String nodeData) throws ParseException {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(NODEHANDLER_URL + "/handleNodes");
		return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(nodeData, MediaType.APPLICATION_JSON));
	}
}
