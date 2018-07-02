package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

import beans.AgentType;
import beans.Host;
import interfaces.AgentInterface;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class AgentsService {

	private Host mainNode;
	
	private ArrayList<Host> slaveNodes;
	private ArrayList<AgentType> allSupportedAgentTypes;
	private ArrayList<AgentType> mySupportedAgentTypes;
	private ArrayList<AgentInterface> runningAgents;

	@PostConstruct
	public void onInit() {
		System.out.println("----------SINGLETON CONSTRUCTED----------");
	}
	
	public void hackz() {
		System.out.println("---------SINGLETON TOUCHED----------");
	}
	
	@PreDestroy
	public void onDelete() {
		System.out.println("----------SINGLETON DELETED----------");
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
