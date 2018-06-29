package app;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import services.GetHostDataService;

@ApplicationPath("/rest")
@Singleton
@Startup
public class App extends Application {
	
	private static String ip;
	
	private static String hostname;

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
}
