package services;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.core.Response;

import org.jboss.as.cli.CommandLineException;
import org.json.simple.parser.ParseException;

import beans.Host;

public class GetHostDataService implements Runnable {

	private RestHandshakeService restHandshakeService;
	
	private Host host;
	
	private String mainNodeDetails;
	
	private String ip;
	
	private String hostname;
	
	public GetHostDataService (String ip, String hostname) {
		this.hostname = hostname;
		this.ip = ip;
		this.mainNodeDetails = "";
		this.host = null;
		this.restHandshakeService = new RestHandshakeService();
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			this.host = getHostData();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (CommandLineException e) {
			e.printStackTrace();
		}
		this.mainNodeDetails = getMainNodeDetails();
	}
	
	public Host getHostData() throws CommandLineException, InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException {
		Host ret = new Host();
		String port;
		String host;
		String portOffset;
		
		port =  ManagementFactory.getPlatformMBeanServer()
				   .getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "port")
				   .toString();
		host = ManagementFactory.getPlatformMBeanServer()
								.getAttribute(new ObjectName("jboss.as:interface=public"), "inet-address")
								.toString();
		
		portOffset = ManagementFactory.getPlatformMBeanServer()
						.getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets"), "port-offset")
						.toString();	
		
		int portValue = Integer.parseInt(port) + Integer.parseInt(portOffset);
		
		String address = this.ip.toString().split("/")[1] + ":" + portValue;
		String alias = host + "/" + this.hostname;
		ret.setAlias(alias);
		ret.setHostAddress(address);
		
		return ret;
    }
	
	public String getMainNodeDetails() {
		String retVal = "";
		try {
			Response resp = this.restHandshakeService.handleNode(this.host.getHostAddress());
			retVal = resp.readEntity(String.class);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
