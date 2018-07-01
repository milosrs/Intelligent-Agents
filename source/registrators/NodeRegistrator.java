package registrators;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;

import beans.AgentType;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;

@Singleton
public class NodeRegistrator {

	private NodeType nodeType;
	private List<Host> slaves;
	private List<AgentType> supportedAgentTypes;
	private Host master;
	private Host thisNodeInfo;
	private ArrayList<AgentInterface> runningAgents;
	
	public boolean setSlavesSentFromMaster(List<Host> slavesList) {
		Host thisNode = thisNodeInfo;
		boolean success = true;
		
		try {
			if(nodeType.equals(NodeType.SLAVE)) {
				if(slavesList.indexOf(thisNode) > -1) {
					slavesList.remove(thisNode);
				}
				
				slaves = slavesList;
			} else {
				success = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		
		return success;
	}
	
	public List<AgentType> addNewAgentTypes(List<AgentType> agentTypes) {
		List<AgentType> nonSupported = new ArrayList<AgentType>();
		
		if (agentTypes != null) {
			try {
				for(AgentType type : agentTypes) {
					if(!supportedAgentTypes.contains(type)) {
						supportedAgentTypes.add(type);
						nonSupported.add(type);
					}
				}	
			} catch(Exception e ) {
				e.printStackTrace();
				System.out.println("Error adding agentTypes");
			}	
		}
		
		return nonSupported;
	}
	
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public List<Host> getSlaves() {
		return slaves;
	}
	public void setSlaves(List<Host> slaves) {
		this.slaves = slaves;
	}
	public Host getMaster() {
		return master;
	}
	public void setMaster(Host master) {
		this.master = master;
	}
	public Host getThisNodeInfo() {
		return thisNodeInfo;
	}
	public void setThisNodeInfo(Host thisNodeInfo) {
		this.thisNodeInfo = thisNodeInfo;
	}

	public List<AgentType> getSupportedAgentTypes() {
		return supportedAgentTypes;
	}

	public void setSupportedAgentTypes(List<AgentType> supportedAgentTypes) {
		this.supportedAgentTypes = supportedAgentTypes;
	}

	public ArrayList<AgentInterface> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(ArrayList<AgentInterface> runningAgents) {
		this.runningAgents = runningAgents;
	}
}
