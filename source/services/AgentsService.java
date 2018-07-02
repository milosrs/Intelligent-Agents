package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import interfaces.AgentInterface;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class AgentsService {

	private Host mainNode;
	
	private Host myHostInfo;
	
	private ArrayList<Host> slaveNodes;
	
	private ArrayList<AgentType> mySupportedAgentTypes;
	
	private ArrayList<AgentTypeDTO> allSupportedAgentTypes;
	
	private ArrayList<AgentInterface> myRunningAgents;
	
	private ArrayList<AID> allRunningAgents;

	@PostConstruct
	public void onInit() {
		System.out.println("----------SINGLETON CONSTRUCTED----------");
	}
	
	public void firstTouch() {
		setSlaveNodes(new ArrayList<Host>());
		setMySupportedAgentTypes(new ArrayList<AgentType>());
		setAllSupportedAgentTypes(new ArrayList<AgentTypeDTO>());
		setMyRunningAgents(new ArrayList<AgentInterface>());
		setAllRunningAgents(new ArrayList<AID>());
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

	public Host getMyHostInfo() {
		return myHostInfo;
	}

	public void setMyHostInfo(Host myHostInfo) {
		this.myHostInfo = myHostInfo;
	}

	public ArrayList<Host> getSlaveNodes() {
		return slaveNodes;
	}

	public void setSlaveNodes(ArrayList<Host> slaveNodes) {
		this.slaveNodes = slaveNodes;
	}

	public ArrayList<AgentType> getMySupportedAgentTypes() {
		return mySupportedAgentTypes;
	}

	public void setMySupportedAgentTypes(ArrayList<AgentType> mySupportedAgentTypes) {
		this.mySupportedAgentTypes = mySupportedAgentTypes;
	}

	public ArrayList<AgentTypeDTO> getAllSupportedAgentTypes() {
		return allSupportedAgentTypes;
	}

	public void setAllSupportedAgentTypes(ArrayList<AgentTypeDTO> allSupportedAgentTypes) {
		this.allSupportedAgentTypes = allSupportedAgentTypes;
	}

	public ArrayList<AgentInterface> getMyRunningAgents() {
		return myRunningAgents;
	}

	public void setMyRunningAgents(ArrayList<AgentInterface> myRunningAgents) {
		this.myRunningAgents = myRunningAgents;
	}

	public ArrayList<AID> getAllRunningAgents() {
		return allRunningAgents;
	}

	public void setAllRunningAgents(ArrayList<AID> allRunningAgents) {
		this.allRunningAgents = allRunningAgents;
	}	
}
