package requestSenders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.AgentType;
import beans.Host;
import interfaces.AgentInterface;

@Stateless
public class RestHandshakeRequestSender {
	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/app";
	
	public Response getRunningAgents(String hostAddress) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + hostAddress + NODE_URL + "/agents/running");
		return webTarget.request(MediaType.APPLICATION_JSON).get();
	}
	
	public Response deleteAgents(Host host, ArrayList<AgentType> agentsToDelete) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + host.getHostAddress() + NODE_URL + "/node/{" + host.getAlias() + "}");
		return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(agentsToDelete, MediaType.APPLICATION_JSON));
	}
	
	@SuppressWarnings("unchecked")
	public List<Host> registerSlaveNode(String url, Host newSlave) {
		List<Host> slaves = null;
		
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + url + NODE_URL + "/handshake/node");
		Response regResp = webTarget.request(MediaType.APPLICATION_JSON)
										.post(Entity.entity(newSlave, MediaType.APPLICATION_JSON));
		
		try {
			Object responseEntity = regResp.getEntity();
			
			if(responseEntity instanceof List<?>) {
				slaves = (List<Host>) responseEntity;
			}	
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error fetching response body for list of slave nodes.");
			slaves = null;
		}
		
		return slaves;
	}
	
	@SuppressWarnings("unchecked")
	public List<AgentType> fetchAgentTypeList(String url) {
		List<AgentType> retList = null;
		
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + url + NODE_URL + "/handshake/agents/classes");
		Response resp = webTarget.request().get();
		
		try {
			Object responseEntity = resp.getEntity();
			
			if(responseEntity instanceof List<?>) {
				retList = (List<AgentType>) responseEntity;
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error fetching response body for agent type list");
			retList = null;
		}
		
		return retList;
	}
	
	public boolean sendNewAgentTypesToSlave(String url, List<AgentType> agents) {
		boolean success = true;

		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(HTTP_URL + url + NODE_URL + "/handshake/agents/classes");
		Response resp = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(agents, MediaType.APPLICATION_JSON));
		
		try {
			Object responseEntity = resp.getEntity();
			
			if(responseEntity instanceof Boolean) {
				success = (Boolean) responseEntity;
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error posting agent types list to slave: " + url);
			success = false;
		}
		
		return success;
	}
	
}