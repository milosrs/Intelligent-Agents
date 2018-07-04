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

	@Inject
	private HandshakeRequestSender requestSender;
	
	private Host mainNode;
	
	private Host myHostInfo;
	
	private List<Host> slaveNodes;
	
	private List<AgentType> mySupportedAgentTypes;
	
	private List<AgentTypeDTO> allSupportedAgentTypes;
	
	private List<AgentInterface> myRunningAgents;
	
	private List<AID> allRunningAgents;
	
	
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
		if(this.nodeType.equals(NodeType.MASTER)) {
			System.out.println("*************** CHECKING SLAVE HEALTH STATUS ****************");
			this.slaveNodes.stream().forEach(slave -> {
				System.out.println("-* ACTION: Checking health status for: " + slave.getAlias());
				
				boolean isAlive = requestSender.isAlive(slave.getHostAddress());
				
				if(!isAlive) {
					System.out.println("-* RESULT: Slave dead, deleting.");
				} else {
					System.out.println("-* RESULT: ALIVE!");
				}
			});
			System.out.println("*************** ENDING SLAVE HEALTH STATUS ****************");	
		}
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
