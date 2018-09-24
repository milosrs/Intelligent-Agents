package services.appConfigServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.jboss.as.cli.CommandLineException;

import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import config.HeartbeatInvoker;
import requestSenders.AdminConsoleRequestSender;
import requestSenders.HandshakeRequestSender;
import services.AgentsService;
import services.ResultPredictionService;

public class GetHostDataService implements Runnable {
	
	private HandshakeRequestSender requestSender;
	private HeartbeatInvoker heartbeat;	
	private JndiTreeParser jndiTreeParser;
	private AgentsService agentsService;
	private Host host;
	private String mainNodeDetails;
	@SuppressWarnings("unused")
	private String ip;
	private String hostname;
	private int portOffset;
	private AdminConsoleRequestSender adminRequestSender;
	@SuppressWarnings("unused")
	private ResultPredictionService resultPredictionService;
	
	public GetHostDataService (String ip, String hostname, AgentsService as, 
			JndiTreeParser jtp, HeartbeatInvoker hbi, HandshakeRequestSender rhs) {
		this.hostname = hostname;
		this.ip = ip;
		this.mainNodeDetails = "";
		this.host = null;
		this.portOffset = 0;
		this.agentsService = as;
		this.jndiTreeParser = jtp;
		this.heartbeat = hbi;
		this.heartbeat.init(this.agentsService);
		this.requestSender = rhs;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(15000);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.host = getHostData();
		} catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
				| ReflectionException | MBeanException | CommandLineException e) {
			e.printStackTrace();
		}
		
		this.mainNodeDetails = getMainNodeDetails();
		boolean isRunning = sendAdminRequest(this.portOffset);
		
		if(isRunning) {
			if(!this.mainNodeDetails.equals(this.host.getHostAddress())) {
				setSlaveData();
				requestSender.registerSlaveNode(this.mainNodeDetails, this.host);
			}
			else {
				setMasterData();
			}
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(heartbeat, 180, 180, TimeUnit.SECONDS);
		}
		else {//kill the app
			System.exit(1);
		}
	}
	
	private void setMasterData() {
		this.agentsService.setMainNode(this.host);
		
		this.agentsService.setMyHostInfo(this.host);					
		this.agentsService.setNodeType(NodeType.MASTER);
		
		ArrayList<AgentType> myAgentTypes = new ArrayList<AgentType>();
		try {
			myAgentTypes = (ArrayList<AgentType>)jndiTreeParser.parse();
		} catch (NamingException e) {
			e.printStackTrace();
		}		
		this.agentsService.setMySupportedAgentTypes(myAgentTypes);
		
		List<AgentTypeDTO> dtos = new ArrayList<AgentTypeDTO>();
		this.agentsService.getMySupportedAgentTypes().stream().forEach(type -> {
			AgentTypeDTO dto = new AgentTypeDTO();
			dto.convertToDTO(type, this.host);
		});
		this.agentsService.setAllSupportedAgentTypes(dtos);
		
		for(Iterator<AgentType> i = myAgentTypes.iterator(); i.hasNext();) {
			AgentTypeDTO listItem = new AgentTypeDTO();
			listItem.convertToDTO(i.next(), this.host);
			this.agentsService.getAllSupportedAgentTypes().add(listItem);
		}
	}

	private void setSlaveData() {
		this.agentsService.setMyHostInfo(this.host);
		this.agentsService.setNodeType(NodeType.SLAVE);
		
		Host mainNode = new Host(this.mainNodeDetails, "mainNode");
		this.agentsService.setMainNode(mainNode);
		
		ArrayList<AgentType> myAgentTypes = new ArrayList<AgentType>();
		try {
			myAgentTypes = (ArrayList<AgentType>)jndiTreeParser.parse();
		} catch (NamingException e) {
			e.printStackTrace();
		}		
		this.agentsService.setMySupportedAgentTypes(myAgentTypes);
		
		List<AgentTypeDTO> dtos = new ArrayList<AgentTypeDTO>();
		this.agentsService.getMySupportedAgentTypes().stream().forEach(type -> {
			AgentTypeDTO dto = new AgentTypeDTO();
			dto.convertToDTO(type, this.host);
		});
		this.agentsService.setAllSupportedAgentTypes(dtos);
		
		for(Iterator<AgentType> i = myAgentTypes.iterator(); i.hasNext();) {
			AgentTypeDTO listItem = new AgentTypeDTO();
			listItem.convertToDTO(i.next(), this.host);
			this.agentsService.getAllSupportedAgentTypes().add(listItem);
		}
	}

	public boolean sendAdminRequest(int portOffset) {
		boolean isUpAndRunning = false;
		int currentManagementPing = 0;
		adminRequestSender = new AdminConsoleRequestSender();
		isUpAndRunning = adminRequestSender.isWildflyRunning(this.host.getHostAddress(), portOffset);
		
		try {
			while(!isUpAndRunning && currentManagementPing < 50) {
				Thread.sleep(500);
				isUpAndRunning = adminRequestSender.isWildflyRunning(null, portOffset);
			}
			if(!isUpAndRunning) {
				System.out.println("Application start failed.\nReason 1: You must have a management user.\nReason 2: Deploy failed");
				System.exit(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isUpAndRunning;
	}
	
	public Host getHostData() throws CommandLineException, InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException {
		Host ret = new Host();
		String port;
		String host;
		int portOffset;
		
		port =  ManagementFactory.getPlatformMBeanServer()
				   .getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "port")
				   .toString();
		host = ManagementFactory.getPlatformMBeanServer()
								.getAttribute(new ObjectName("jboss.as:interface=public"), "inet-address")
								.toString();
		
		portOffset = Integer.parseInt(ManagementFactory.getPlatformMBeanServer()
						.getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets"), "port-offset")
						.toString());
		int portValue = Integer.parseInt(port) + portOffset;
		
		this.portOffset = portOffset;
		this.agentsService.setPortOffset(portOffset);
		
		String address = host.split(":")[0] + ":" + portValue;
		String alias = host + "|" + this.hostname;
		ret.setAlias(alias);
		ret.setHostAddress(address);
		
		return ret;
    }
	
	public String getMainNodeDetails() {
		String resourcePath = "mainNodeData/mainNodeInfo.txt";
		URL url = this.getClass().getClassLoader().getResource(resourcePath);
		System.out.println("READING MAIN NODE DATA FROM: " + url.getPath());
		InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
		String line = null;
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource));
			line = bufferedReader.readLine();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(line == null) {
			System.out.println("ERROR WHLE READING THE MAIN NODE DATA!");
			return "ERROR";
		}
		else
			return line;
	}
}
