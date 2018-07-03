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
import config.HeartbeatInvoker;
import requestSenders.HandshakeRequestSender;
import services.AgentsService;
import services.GetHostDataService;
import services.JndiTreeParser;

@ApplicationPath("/rest")
@Singleton
@Startup
public class App extends Application {
	
	private static String ip;
	
	private static String hostname;

	@Inject
	private AgentsService agentsService;
	
	@Inject
	private HandshakeRequestSender requestSender;
		
	@Inject
	private AgentsService as;	
	
	@Inject
	private JndiTreeParser jtp;
	
	@Inject
	private HandshakeRequestSender rhs;
	
	@PostConstruct
	public void init() {
		try {
			
			ip = InetAddress.getLocalHost().toString();
			hostname = InetAddress.getLocalHost().getHostName();
			System.out.println("Hostname/IP: " + ip + " Hostname: " + hostname);
			
			//touch singleton to work properly
			as.firstTouch();
			
			//await for jboss to start and then get the port and initialize the node-handshake
			GetHostDataService getHostDataService = new GetHostDataService(ip, hostname, as, jtp, new HeartbeatInvoker(), rhs);
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

				Response resp = requestSender.deleteAgents(item, agentsService.getMySupportedAgentTypes());
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
				Response resp = requestSender.deleteAgents(main, agentsService.getMySupportedAgentTypes());
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
