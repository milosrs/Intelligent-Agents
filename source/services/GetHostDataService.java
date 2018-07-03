package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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

import org.jboss.as.cli.CommandLineException;

import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import config.HeartbeatInvoker;
import requestSenders.AdminConsoleRequestSender;
import requestSenders.HandshakeRequestSender;

public class GetHostDataService implements Runnable {
	
	private HandshakeRequestSender requestSender;
	private HeartbeatInvoker heartbeat;	
	private JndiTreeParser jndiTreeParser;
	private AgentsService agentsService;
	private Host host;
	private String mainNodeDetails;
	private String ip;
	private String hostname;
	private int portOffset;
	private AdminConsoleRequestSender adminRequestSender;
	
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
		
		//await for jboss startup
		try {
			Thread.sleep(15000);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//try reading host data from socket-bindings
		try {
			host = getHostData();
		} catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
				| ReflectionException | MBeanException | CommandLineException e) {
			e.printStackTrace();
		}
		
		//check if server is up and running
		boolean isRunning = sendAdminRequest(this.portOffset);
		
		if(isRunning) {//startup settings
			this.mainNodeDetails = getMainNodeDetails();
			
			//i am a slave node, initialize handshake
			if(!this.mainNodeDetails.equals(this.host.getHostAddress())) {
				//set my data
				setSlaveData();
				
				//start rest handshake
				requestSender.registerSlaveNode(this.mainNodeDetails, this.host);
			}
			else { //i am the master, save my data
				setMasterData();
				
				//initialize heartbeat
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
				executor.scheduleAtFixedRate(heartbeat, 180, 180, TimeUnit.SECONDS);
			}	
		}
		else {//kill the app
			System.exit(1);
		}
	}
	
	private void setMasterData() {
		//add the main node data
		this.agentsService.setMainNode(this.host);
		
		//add my host data
		this.agentsService.setMyHostInfo(this.host);					
		this.agentsService.setNodeType(NodeType.MASTER);
		
		//get and set my agent types
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
		//add my host data
		this.agentsService.setMyHostInfo(this.host);
		this.agentsService.setNodeType(NodeType.SLAVE);
		
		//add the main node data
		Host mainNode = new Host(this.mainNodeDetails, "mainNode");
		this.agentsService.setMainNode(mainNode);
		
		//get and set my agent types
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
		isUpAndRunning = adminRequestSender.isWildflyRunning(null, portOffset);
		
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
		
//		String address = this.ip.toString().split("/")[1] + ":" + portValue;	Domain scenario
		String address = "localhost:" + portValue;
		String alias = host + "/" + this.hostname;
		ret.setAlias(alias);
		ret.setHostAddress(address);
		
		return ret;
    }
	
	@SuppressWarnings("resource")
	public String getMainNodeDetails() {
		String absolutePath = GetHostDataService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    String fullResourcePath = newPath + "/../mainNodeData/mainNodeInfo.txt";
		File file = new File(fullResourcePath);
		
		String line = null;
		try {
			FileReader nodesData = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(nodesData);
			line = bufferedReader.readLine();

	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
		if(line == null) {
			System.out.println("ERROR WHLE READING THE MAIN NODE DATA!");
			return "ERROR";
		}
		else
			return "localhost:" + line;
	}
}
