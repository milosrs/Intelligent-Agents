package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.as.cli.CommandLineException;

import beans.Host;
import controllers.RestController;

@ApplicationPath("/rest")
@Singleton
@Startup
public class App extends Application {

	private static String port;
	
	private static String host;
	
	private static String ip;
	
	private static String hostname;
	
	@Inject
	private RestController restController;
	
	@PostConstruct
	public void init() {
		try {
			ip = InetAddress.getLocalHost().toString();
			hostname = InetAddress.getLocalHost().getHostName();
			System.out.println("Hostname/IP: " + ip + " Hostname:" + hostname);
			
			boolean isFirstNode = true;
			
			//doesn't work on startup, check for other solutions
			Host me = getHost();
			File file = new File("/nodesData/nodes.txt");
			FileReader nodesData = new FileReader(file);
			String line;
			try (BufferedReader bufferedReader = new BufferedReader(nodesData)) {
			      if((line = bufferedReader.readLine()) != null)
			    	  isFirstNode = false;
		    }
			
			if(isFirstNode) {
				//set master
				FileWriter fw = new FileWriter(file);
				fw.write(me.getHostAddress() + ":" + port);
			}
			else {
				//send handshake to master (line has the data!)
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Host getHost() throws CommandLineException, InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException {
		Host ret = new Host();
		
		port =  ManagementFactory.getPlatformMBeanServer()
				   .getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "port")
				   .toString();
		host = ManagementFactory.getPlatformMBeanServer()
								.getAttribute(new ObjectName("jboss.as:interface=public"), "inet-address")
								.toString();
		
		String address = ip.toString().split("/")[1] + ":" + port;
		String alias = host + "/" + hostname;
		ret.setAlias(alias);
		ret.setHostAddress(address);
		
		return ret;
    }
}
