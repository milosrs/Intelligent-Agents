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
import services.ResultPredictionService;

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
	
//	@Inject
//	private ResultPredictionService rps;
	
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
}
