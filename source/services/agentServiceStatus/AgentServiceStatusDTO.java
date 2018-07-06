package services.agentServiceStatus;

import java.util.List;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;

public class AgentServiceStatusDTO {

	private int portOffset;
	private NodeType nodeType;
	private Host mainNode;
	private Host myHostInfo;
	private List<Host> slaveNodes;
	private List<AgentType> mySupportedAgentTypes;
	private List<AgentTypeDTO> allSupportedAgentTypes;
	private List<AgentInterface> myRunningAgents;
	private List<AID> allRunningAgents;
	
	public AgentServiceStatusDTO(int portOffset, NodeType nodeType, Host mainNode, Host myHostInfo,
			List<Host> slaveNodes, List<AgentType> mySupportedAgentTypes, List<AgentTypeDTO> allSupportedAgentTypes,
			List<AgentInterface> myRunningAgents, List<AID> allRunningAgents) {
		super();
		this.portOffset = portOffset;
		this.nodeType = nodeType;
		this.mainNode = mainNode;
		this.myHostInfo = myHostInfo;
		this.slaveNodes = slaveNodes;
		this.mySupportedAgentTypes = mySupportedAgentTypes;
		this.allSupportedAgentTypes = allSupportedAgentTypes;
		this.myRunningAgents = myRunningAgents;
		this.allRunningAgents = allRunningAgents;
	}
	
	
}
