package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.NamingException;

import org.jboss.as.cli.CommandLineException;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.PongAgent;
import beans.enums.NodeType;
import registrators.NodeRegistrator;
import requestSenders.AdminConsoleRequestSender;
import interfaces.AgentInterface;
import requestSenders.RestHandshakeRequestSender;

public class GetHostDataService implements Runnable {
	@Inject
	private RestHandshakeRequestSender requestSender;
	@Inject
	private NodeRegistrator nodeRegistrator;
	private JndiTreeParser jndiTreeParser;
	private AgentsService agentsService;
	private Host host;
	private String mainNodeDetails;
	private String ip;
	private String hostname;
	private final int maxPingTrials = 100;
	private AdminConsoleRequestSender adminRequestSender;
	
	public GetHostDataService (String ip, String hostname, AgentsService as, JndiTreeParser jtp) {
		this.hostname = hostname;
		this.ip = ip;
		this.mainNodeDetails = "";
		this.host = null;
		this.agentsService = as;
		this.jndiTreeParser = jtp;
	}
	
	@Override
	public void run() {
		
		try {
			host = getHostData();
		} catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
				| ReflectionException | MBeanException | CommandLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mainNodeDetails = getMainNodeDetails();
		
		//i am a slave node, initialize handshake
		if(!this.mainNodeDetails.equals(this.host.getHostAddress())) {
			//add my host data
			this.agentsService.setMyHostInfo(this.host);
			
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
			
			for(Iterator<AgentType> i = myAgentTypes.iterator(); i.hasNext();) {
				AgentTypeDTO listItem = new AgentTypeDTO(i.next(), this.host);
				this.agentsService.getAllSupportedAgentTypes().add(listItem);
			}
			
			//start rest handshake
			setSlavery(mainNode);
		}
		else { //i am the master, save my data
			//add the main node data
			this.agentsService.setMainNode(this.host);
			
			//add my host data
			this.agentsService.setMyHostInfo(this.host);
			
			/*//mock of runningAgents
			AgentType pong = new AgentType("pong1", "PONG");
			AID aid = new AID("pongAgent", this.host, pong);
			ArrayList<AID> allAgentsList = new ArrayList<AID>();			
			allAgentsList.add(aid);
			this.agentsService.setAllRunningAgents(allAgentsList);
			
			PongAgent pongAgent = new PongAgent(aid);
			ArrayList<AgentInterface> agentsList = new ArrayList<AgentInterface>();
			agentsList.add(pongAgent);
			this.agentsService.setMyRunningAgents(agentsList);
			//end mock*/
			
			
			//get and set my agent types
			ArrayList<AgentType> myAgentTypes = new ArrayList<AgentType>();
			try {
				myAgentTypes = (ArrayList<AgentType>)jndiTreeParser.parse();
			} catch (NamingException e) {
				e.printStackTrace();
			}		
			this.agentsService.setMySupportedAgentTypes(myAgentTypes);
			
			for(Iterator<AgentType> i = myAgentTypes.iterator(); i.hasNext();) {
				AgentTypeDTO listItem = new AgentTypeDTO(i.next(), this.host);
				this.agentsService.getAllSupportedAgentTypes().add(listItem);
			}
			
//			setMastery(me);
		}
	}
	
	private void setMastery(Host mainNode) {
		nodeRegistrator.setNodeType(NodeType.MASTER);
		nodeRegistrator.setMaster(mainNode);
		nodeRegistrator.setThisNodeInfo(mainNode);
	}

	private void setSlavery(Host mainNode) {
		nodeRegistrator.setNodeType(NodeType.SLAVE);
		nodeRegistrator.setMaster(mainNode);
		nodeRegistrator.setThisNodeInfo(host);
		requestSender.registerSlaveNode(this.mainNodeDetails, this.host);
	}

	public Host getHostData() throws CommandLineException, InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException {
		Host ret = new Host();
		String port;
		String host;
		String portOffset;
		boolean isUpAndRunning = false;
		
		adminRequestSender = new AdminConsoleRequestSender();
		isUpAndRunning = adminRequestSender.isWildflyRunning();
		
		try {
			while(!isUpAndRunning) {
				Thread.sleep(500);
				isUpAndRunning = adminRequestSender.isWildflyRunning();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
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
			return line;
	}
}
