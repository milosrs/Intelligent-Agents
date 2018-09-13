package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.Message;
import beans.enums.NodeType;
import controllers.WebSocketController;
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
	private boolean hasMasterNodeDisconnected;
	
	@Inject
	private HandshakeRequestSender requestSender;
	
	private Host mainNode;
	
	private Host myHostInfo;
	
	private List<Host> slaveNodes;
	
	private List<AgentType> mySupportedAgentTypes;
	
	private List<AgentTypeDTO> allSupportedAgentTypes;
	
	private List<AgentInterface> myRunningAgents;
	
	private List<AID> myRunningAgentsAID;
	
	private List<AID> allRunningAgents;
	
	private ObjectMapper mapper;
	
	@PostConstruct
	public void init() {
		this.restClient = ClientBuilder.newClient();
		mapper = new ObjectMapper();
		setSlaveNodes(new ArrayList<Host>());
		setMySupportedAgentTypes(new ArrayList<AgentType>());
		setAllSupportedAgentTypes(new ArrayList<AgentTypeDTO>());
		setMyRunningAgents(new ArrayList<AgentInterface>());
		setAllRunningAgents(new ArrayList<AID>());
		setMyRunningAgentsAID(new ArrayList<AID>());
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
	
	public List<AgentTypeDTO> addNewAgentTypes(List<AgentTypeDTO> agentTypes) throws JsonProcessingException, IOException {
		if (agentTypes != null && agentTypes != null) {
			agentTypes.forEach(type -> {
				boolean shouldAdd = type.getHostAddress() != null;
				
				if(shouldAdd) {
					for(AgentTypeDTO t : this.allSupportedAgentTypes) {
						if(t.getHostAddress().equals(type.getHostAddress()) && t.getName().equals(type.getName())) {
							shouldAdd = false;
							break;
						}
					}
					
					if(shouldAdd) {
						this.allSupportedAgentTypes.add(type);
					}
				}
			});
			System.out.println("Node: " + this.myHostInfo.getHostAddress());
			printAllSupportedAgents();
		}
		
		Iterator<Session> iterator2 = WebSocketController.sessions.iterator();
		while(iterator2.hasNext()) {
			Session s = iterator2.next();
			s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("addTypes", mapper.writeValueAsString(agentTypes))));
		}
		
		return this.allSupportedAgentTypes;
	}
	
	/***
	 * Metoda koja treba da se poziva za heartbeat protokol
	 */
	public void checkSlavesHealth() {
		List<Host> toDelete = new ArrayList<Host>();
		
		if(this.nodeType.equals(NodeType.SLAVE)) {
			boolean masterAlive = requestSender.isAlive(mainNode.getHostAddress());
			
			if(!masterAlive) {
				disconnectNode(mainNode);
				hasMasterNodeDisconnected = true;
			} else if(hasMasterNodeDisconnected) {
					requestSender.registerSlaveNode(this.mainNode.getHostAddress(), this.myHostInfo);
			}
		}
		
		this.slaveNodes.stream().forEach(slave -> {
			boolean isAlive = requestSender.isAlive(slave.getHostAddress());

			if (!isAlive) {
				toDelete.add(slave);
			}
		});
		
		for(int i=0; i<toDelete.size(); i++) {
			disconnectNode(toDelete.get(i));
			this.slaveNodes.remove(i);
		}
	}
	
	private void disconnectNode(Host slave) {
		this.slaveNodes.stream().forEach(slv -> {
			if(!slave.getHostAddress().equals(slv.getHostAddress())) {
				requestSender.deleteBadNode(slv.getHostAddress(), slave.getHostAddress());
			}
		});
		
		try {
			deleteNode(slave.getHostAddress());	
		} catch(Exception e ) {
			e.printStackTrace();
			System.out.println("!!!!!!!!!!!!!!!!!!!! Error disconnecting node. !!!!!!!!!!!!!!!!!!!");
		}
		
	}
	
	private boolean deleteBadNode(String url, String nodeAlias) {
		boolean success = true;
		
		webTarget = restClient.target(HTTP_URL + url + NODE_URL + "/node/" + nodeAlias);
		Response resp = webTarget.request(MediaType.APPLICATION_JSON).delete();
		
		try {
			success = resp.readEntity(boolean.class);
		} catch(Exception e) {
			success = resp.getStatus() == 200;
		}
		
		return success;
	}
	
	public boolean deleteNode(String alias) throws JsonProcessingException, IOException {
		boolean retVal = true;
		List<AgentTypeDTO> toDelete = new ArrayList<AgentTypeDTO>();
		List<AID> runningAgentsToDelete = new ArrayList<AID>();
		mapper = new ObjectMapper();
		
		this.allSupportedAgentTypes.forEach(type -> {
			try {
				if(type.getHostAddress().equals(alias)) {
					toDelete.add(type);
				}	
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		this.allRunningAgents.forEach(agent -> {
			Host host = agent.getHost();
			
			if(alias.equals(host.getHostAddress())) {
				runningAgentsToDelete.add(agent);
			}
		});
		
		Iterator<Session> iterator = WebSocketController.sessions.iterator();
		while(iterator.hasNext()) {
			Session s = iterator.next();
			s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("deleteRunning", mapper.writeValueAsString(runningAgentsToDelete))));
		}
		Iterator<Session> iterator2 = WebSocketController.sessions.iterator();
		while(iterator2.hasNext()) {
			Session s = iterator2.next();
			s.getBasicRemote().sendText(mapper.writeValueAsString(new Message("deleteTypes", mapper.writeValueAsString(toDelete))));
		}
		
		retVal = this.getAllRunningAgents().removeAll(runningAgentsToDelete);
		retVal = this.getAllSupportedAgentTypes().removeAll(toDelete);	//Delete supported agents
		retVal = this.getSlaveNodes().removeIf(x -> x.getHostAddress().equals(alias));	//Delete node
			
		return retVal;
	}

	public void firstTouch() {
		init();
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

	public List<AID> getMyRunningAgentsAID() {
		return myRunningAgentsAID;
	}

	public void setMyRunningAgentsAID(List<AID> myRunningAgentsAID) {
		this.myRunningAgentsAID = myRunningAgentsAID;
	}
	
	private void printAllSupportedAgents() {
		this.allSupportedAgentTypes.forEach(type -> {
			System.out.println(type.getName() + " -> " + type.getHostAddress());
		});
	}
}
