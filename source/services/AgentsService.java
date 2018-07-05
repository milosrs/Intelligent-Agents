package services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;
import requestSenders.AdminConsoleRequestSender;
import requestSenders.HandshakeRequestSender;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class AgentsService {

	private int portOffset;
	
	private NodeType nodeType;

	private Client restClient;
	private WebTarget webTarget;
	private static String HTTP_URL = "http://";
	private static String NODE_URL = "/Inteligent_Agents/rest/handshake";
	
	@Inject
	private HandshakeRequestSender requestSender;
	
	private Host mainNode;
	
	private Host myHostInfo;
	
	private List<Host> slaveNodes;
	
	private List<AgentType> mySupportedAgentTypes;
	
	private List<AgentTypeDTO> allSupportedAgentTypes;
	
	private List<AgentInterface> myRunningAgents;
	
	private List<AID> allRunningAgents;
	
	@PostConstruct
	public void init() {
		this.restClient = ClientBuilder.newClient();
	}
	
	@PreDestroy
	public void disconnectFromNetwork() {
		System.out.println("***** Disconnecting from network *****");
		if(nodeType.equals(NodeType.SLAVE)) {
			deleteBadNode(mainNode.getHostAddress(), myHostInfo.getHostAddress());
		}
		
		slaveNodes.stream().forEach(s -> deleteBadNode(s.getHostAddress(), myHostInfo.getHostAddress()));
		System.out.println("***** GGWP *****");
	}
	
	public boolean setSlavesSentFromMaster(List<Host> slavesList) {
		Host thisNode = this.myHostInfo;
		boolean success = true;
		
		try {
			if(nodeType.equals(NodeType.SLAVE)) {
				if(slavesList.indexOf(thisNode) > -1) {
					slavesList.remove(thisNode);
				}
				
				this.slaveNodes = slavesList;
			} else {
				success = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		
		return success;
	}
	
	public List<AgentTypeDTO> addNewAgentTypes(List<AgentTypeDTO> agentTypes) {
		List<AgentTypeDTO> nonSupported = new ArrayList<AgentTypeDTO>();
		
		if (agentTypes != null) {
			try {
				for(AgentTypeDTO type : agentTypes) {
					this.allSupportedAgentTypes.stream().forEach(alltype -> {
						if(!type.getModule().equals(alltype.getModule()) 
							&& !type.getName().equals(alltype.getName())) {
							allSupportedAgentTypes.add(type);
							nonSupported.add(type);
						}
					});
				}
			} catch(Exception e ) {
				e.printStackTrace();
				System.out.println("Error adding agentTypes");
				return null;
			}	
		}
		
		return nonSupported;
	}
	
	/***
	 * Metoda koja treba da se poziva za heartbeat protokol
	 */
	public void checkSlavesHealth() {
		System.out.println("*************** CHECKING SLAVE HEALTH STATUS ****************");
		
		List<Host> toDelete = new ArrayList<Host>();
		
		if(this.nodeType.equals(NodeType.SLAVE)) {
			boolean masterAlive = requestSender.isAlive(mainNode.getHostAddress());
			
			if(!masterAlive) {
				System.out.println("Master is dead, disconnecting.");
				disconnectNode(mainNode);
			}
		}
		
		this.slaveNodes.stream().forEach(slave -> {
			System.out.println("-* ACTION: Checking health status for: " + slave.getAlias());

			boolean isAlive = requestSender.isAlive(slave.getHostAddress());

			if (!isAlive) {
				System.out.println("-* RESULT: Slave dead, deleting.");
				toDelete.add(slave);
			} else {
				System.out.println("-* RESULT: ALIVE!");
			}
		});
		
		for(int i=0; i<toDelete.size(); i++) {
			disconnectNode(toDelete.get(i));
		}
		
		System.out.println("*************** ENDING SLAVE HEALTH STATUS ****************");
	}
	
	private void disconnectNode(Host slave) {
		this.slaveNodes.stream().forEach(slv -> {
			if(!slave.equals(slv)) {
				requestSender.deleteBadNode(slv.getHostAddress(), slave.getHostAddress());
			}
		});
		
		deleteNode(slave.getHostAddress());
	}
	
	private boolean deleteBadNode(String url, String nodeAlias) {
		boolean success = true;
		
		webTarget = restClient.target(HTTP_URL + url + NODE_URL + "/node/" + nodeAlias);
		Response resp = webTarget.request(MediaType.APPLICATION_JSON).delete();
		
		try {
			success = resp.readEntity(boolean.class);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error deleting node.");
			success = false;
		}
		
		return success;
	}
	
	public boolean deleteNode(String alias) {
		boolean retVal = true;
		
		List<AgentTypeDTO> toDelete = new ArrayList<AgentTypeDTO>();
		List<AID> runningAgentsToDelete = new ArrayList<AID>();
		this.getAllSupportedAgentTypes().forEach(type -> {
			if(type.getAlias().equals(alias) || type.getHostAddress().equals(alias)) {
				toDelete.add(type);
			}
		});
		this.getAllRunningAgents().forEach(agent -> {
			Host host = agent.getHost();
			
			if(alias.equals(host.getHostAddress())) {
				runningAgentsToDelete.add(agent);
			}
		});
		
		retVal = this.getAllRunningAgents().removeAll(runningAgentsToDelete);
		retVal = this.getAllSupportedAgentTypes().removeAll(toDelete);	//Delete supported agents
		retVal = this.getSlaveNodes().removeIf(x -> x.getHostAddress().equals(alias));	//Delete node
			
		return retVal;
	}

	public void firstTouch() {
		setSlaveNodes(new ArrayList<Host>());
		setMySupportedAgentTypes(new ArrayList<AgentType>());
		setAllSupportedAgentTypes(new ArrayList<AgentTypeDTO>());
		setMyRunningAgents(new ArrayList<AgentInterface>());
		setAllRunningAgents(new ArrayList<AID>());
		System.out.println("---------SINGLETON TOUCHED----------");
	}
	
	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public int getPortOffset() {
		return portOffset;
	}

	public void setPortOffset(int portOffset) {
		this.portOffset = portOffset;
	}

	public Host getMainNode() {
		return mainNode;
	}

	public void setMainNode(Host mainNode) {
		this.mainNode = mainNode;
	}

	public Host getMyHostInfo() {
		return myHostInfo;
	}

	public void setMyHostInfo(Host myHostInfo) {
		this.myHostInfo = myHostInfo;
	}

	public List<Host> getSlaveNodes() {
		return slaveNodes;
	}

	public void setSlaveNodes(List<Host> slaveNodes) {
		this.slaveNodes = slaveNodes;
	}

	public List<AgentType> getMySupportedAgentTypes() {
		return mySupportedAgentTypes;
	}

	public void setMySupportedAgentTypes(List<AgentType> mySupportedAgentTypes) {
		this.mySupportedAgentTypes = mySupportedAgentTypes;
	}

	public List<AgentTypeDTO> getAllSupportedAgentTypes() {
		return allSupportedAgentTypes;
	}

	public void setAllSupportedAgentTypes(List<AgentTypeDTO> allSupportedAgentTypes) {
		this.allSupportedAgentTypes = allSupportedAgentTypes;
	}

	public List<AgentInterface> getMyRunningAgents() {
		return myRunningAgents;
	}

	public void setMyRunningAgents(List<AgentInterface> myRunningAgents) {
		this.myRunningAgents = myRunningAgents;
	}

	public List<AID> getAllRunningAgents() {
		return allRunningAgents;
	}

	public void setAllRunningAgents(List<AID> allRunningAgents) {
		this.allRunningAgents = allRunningAgents;
	}	
}
