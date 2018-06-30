package app;

import java.net.InetAddress;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import beans.Host;
import requestSenders.RestHandshakeRequestSender;
import services.AgentsService;
import services.GetHostDataService;

@ApplicationPath("/rest")
@Singleton
@Startup
public class App extends Application {
	
	private static String ip;
	
	private static String hostname;

	@Inject
	private AgentsService agentsService;
	
	@Inject
	private RestHandshakeRequestSender requestSender;
	
	@PostConstruct
	public void init() {
		try {
			
			ip = InetAddress.getLocalHost().toString();
			hostname = InetAddress.getLocalHost().getHostName();
			System.out.println("Hostname/IP: " + ip + " Hostname: " + hostname);
			
			//await for jboss to start and then get the port and initialize the node-handshake
			GetHostDataService getHostDataService = new GetHostDataService(ip, hostname);
			Thread t = new Thread(getHostDataService);
			t.start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void destroy() {
		try {
			
			//delete node data from other slaves (all slaves if i am the main node)
			for (Iterator<Host> h = agentsService.getSlaveNodes().iterator(); h.hasNext();) {
				Host item = h.next();
				Response resp = requestSender.deleteAgents(item, agentsService.getMyAgents());
				boolean respInfo = resp.readEntity(Boolean.class);
				
				if(respInfo)
					System.out.println("Deleting data from " + item.getAlias() + " SUCCEDED!");
				else
					System.out.println("Deleting data from " + item.getAlias() + " FAILED!");
			}
			
			//slave shutdown (i am not the main node)
			if(!agentsService.getMainNode().getHostAddress().equals("ME")) {
				//delete node data from main node
				Host main = agentsService.getMainNode();
				Response resp = requestSender.deleteAgents(main, agentsService.getMyAgents());
				boolean respInfo = resp.readEntity(Boolean.class);
				
				if(respInfo)
					System.out.println("Deleting data from " + main.getAlias() + " SUCCEDED!");
				else
					System.out.println("Deleting data from " + main.getAlias() + " FAILED!");
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
