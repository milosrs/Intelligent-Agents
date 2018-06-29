package services;

import java.util.ArrayList;

import javax.ejb.Singleton;

import beans.Host;
import interfaces.AgentInterface;

@Singleton
public class AgentsService {

	private Host mainNode;
	
	private ArrayList<Host> slaveNodes;
	
	private ArrayList<AgentInterface> myAgents;
	
	private ArrayList<AgentInterface> allAgents;
	
	private ArrayList<AgentInterface> runningAgents;

	public AgentsService() {
		this.slaveNodes = new ArrayList<Host>();		
	}
	
	public AgentsService(Host mainNode, ArrayList<Host> slaveNodes, ArrayList<AgentInterface> myAgents,
			ArrayList<AgentInterface> allAgents,
			ArrayList<AgentInterface> runningAgents) {
		super();
		this.mainNode = mainNode;
		this.slaveNodes = slaveNodes;
		this.myAgents = myAgents;
		this.allAgents = allAgents;
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

	public ArrayList<AgentInterface> getMyAgents() {
		return myAgents;
	}

	public void setMyAgents(ArrayList<AgentInterface> myAgents) {
		this.myAgents = myAgents;
	}
	
	public ArrayList<AgentInterface> getAllAgents() {
		return allAgents;
	}

	public void setAllAgents(ArrayList<AgentInterface> allAgents) {
		this.allAgents = allAgents;
	}

	public ArrayList<AgentInterface> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(ArrayList<AgentInterface> runningAgents) {
		this.runningAgents = runningAgents;
	}		
}
