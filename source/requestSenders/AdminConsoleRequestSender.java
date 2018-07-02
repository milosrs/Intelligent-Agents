package requestSenders;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class AdminConsoleRequestSender {
	private final String[] DEPLOYMENT_CHECK_URLS = {"http://localhost:9990/management/deployment/Inteligent_Agents-0.0.1-SNAPSHOT.war", 
													"http://localhost:9990/management/deployment/Inteligent_Agents.war"};
	private Client restClient;
	private WebTarget webTarget;
	
	public boolean isWildflyRunning() {
		
		boolean success = getRequestStatus(DEPLOYMENT_CHECK_URLS[0]) == 200;
		
		if(!success) {
			success = getRequestStatus(DEPLOYMENT_CHECK_URLS[1]) == 200;
		}
		
		return success;
	}
	
	private int getRequestStatus(String url) {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(url);
		Response resp = webTarget.request().get();
		System.out.println(resp.getStatus());
		
		return resp.getStatus();
	}
}
