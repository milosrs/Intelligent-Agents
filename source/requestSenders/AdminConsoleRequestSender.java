package requestSenders;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class AdminConsoleRequestSender {
	private String STATUS_URL = "http://{0}:{1}/management?operation=attribute&name=server-state";
	private Client restClient;
	private WebTarget webTarget;
	private HttpAuthenticationFeature feature;
	private final String EXPECTED = "running";
	
	public AdminConsoleRequestSender() {
		feature = HttpAuthenticationFeature.digest("riki", "root");
		restClient = ClientBuilder.newClient();
		restClient.register(feature);
	}
	
	public boolean isWildflyRunning(String ipAddress, int portOffset) {
		if (ipAddress == null) {
			STATUS_URL = STATUS_URL.replace("{0}", "localhost");
		} else {
			if(ipAddress.contains(":")) {
				ipAddress = ipAddress.split(":")[0];
			}
			STATUS_URL = STATUS_URL.replace("{0}", ipAddress);
		}

		STATUS_URL = STATUS_URL.replace("{1}", Integer.toString(9990 + portOffset));
		
		boolean success = isUpAndRunning(STATUS_URL);
		
		return success;
	}
	
	private boolean isUpAndRunning(String url) {
		webTarget = restClient.target(url);
		Response resp = webTarget.request().get();
		int status = resp.getStatus();
		String result = resp.readEntity(String.class).replaceAll("\"", "");
		System.out.println("*********** Management returned: " + resp.getStatus() + " ******************\n" 
							+ "Application status: " + result + "\n***************************************");
		
		return status == 200 && result.equals(EXPECTED);
	}
}
