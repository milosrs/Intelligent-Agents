package services;

import java.util.ArrayList;

import javax.ejb.Singleton;

import beans.AgentType;
import beans.Host;
import interfaces.AgentInterface;

@Singleton
public class AgentsService {

	private Host mainNode;
	
	private ArrayList<Host> slaveNodes;
	private ArrayList<AgentType> allSupportedAgentTypes;
	private ArrayList<AgentType> mySupportedAgentTypes;
	private ArrayList<AgentInterface> runningAgents;

	public AgentsService() {
		this.slaveNodes = new ArrayList<Host>();
		
		this.runningAgents = new ArrayList<AgentInterface>();
	}
	
	public AgentsService(Host mainNode, ArrayList<Host> slaveNodes, ArrayList<AgentType> allSupportedAgentTypes,
			ArrayList<AgentType> mySupportedAgentTypes,
			ArrayList<AgentInterface> runningAgents) {
		super();
		this.mainNode = mainNode;
		this.slaveNodes = slaveNodes;
		this.allSupportedAgentTypes = allSupportedAgentTypes;
		this.mySupportedAgentTypes = mySupportedAgentTypes;
		this.runningAgents = runningAgents;
	}

	public Host getMainNode() {
		return mainNode;
	}

	public void setMainNode(Host mainNode) {
		this.mainNode = mainNode;
	}

	public ArrayList<Host> getSlaveNodes() {
		return slaveNodes;
	}

	public void setSlaveNodes(ArrayList<Host> slaveNodes) {
		this.slaveNodes = slaveNodes;
	}
	
	public ArrayList<AgentType> getAllSupportedAgentTypes() {
		return allSupportedAgentTypes;
	}

	public void setAllSupportedAgentTypes(ArrayList<AgentType> allSupportedAgentTypes) {
		this.allSupportedAgentTypes = allSupportedAgentTypes;
	}

	public ArrayList<AgentType> getMySupportedAgentTypes() {
		return mySupportedAgentTypes;
	}

	public void setMySupportedAgentTypes(ArrayList<AgentType> mySupportedAgentTypes) {
		this.mySupportedAgentTypes = mySupportedAgentTypes;
	}

	public ArrayList<AgentInterface> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(ArrayList<AgentInterface> runningAgents) {
		this.runningAgents = runningAgents;
	}		
}
