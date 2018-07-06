package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;
import requestSenders.HandshakeRequestSender;

@Stateful
public class HandshakeService {
	@Inject
	private HandshakeRequestSender requestSender;
	@Inject
	private AgentsService agentsService;
	private List<AgentTypeDTO> supported = null;

	public List<Host> startHandshake(Host newSlave) {
		boolean isSuccess = true;
		
		List<Host> slaves = agentsService.getSlaveNodes();
			
		for (Host slave : slaves) {
			if (slave.equals(newSlave)) {
				isSuccess = false;
				break;
			}
		}

		if (isSuccess) {
			if(agentsService.getNodeType().equals(NodeType.MASTER)) {
				isSuccess = tryHandshake(newSlave);	
			}
			if(isSuccess) {
				agentsService.getSlaveNodes().add(newSlave);	
			} else {
				System.out.println("ERROR: Handshake failed. Initializing rollback.");
				rollback(newSlave);
			}
		}
		
		return agentsService.getSlaveNodes();
	}
	
	private boolean tryHandshake(Host newSlave) {
		boolean isSuccess = true;
		int currentHandshakeAttempt = 0;
		int option = 0;
		
		if(agentsService.getNodeType().equals(NodeType.MASTER)) {
			System.out.println("Initializing handshake for slave: " + newSlave.getAlias());
			while(currentHandshakeAttempt < 3 && option <= 5) {
				try {
					switch(option) {
					case 0: {
						supported = requestSender.fetchAgentTypeList(newSlave.getHostAddress());
						agentsService.addNewAgentTypes(supported);
						
						break;
					}
					case 1: isSuccess = sendRegisteredSlaveToSlaves(newSlave); break;
					case 2: isSuccess = sendNewAgentTypesToAllSlaves(supported);  break;
					case 3: isSuccess = sendSlaveListToNewSlave(newSlave.getHostAddress()); break;
					case 4: isSuccess = sendAgentTypesToNewSlave(newSlave.getHostAddress(), agentsService.getAllSupportedAgentTypes()); break;
					case 5: isSuccess = sendRunningAgentsToNewSlave(newSlave.getHostAddress(), agentsService.getAllRunningAgents()); break;
					default: System.out.println("Handshake successfull!"); break;
					}
				} catch(Exception e) {
					e.printStackTrace();
					isSuccess = false;
				}
				
				if(!isSuccess) {
					currentHandshakeAttempt++;
					try {
						System.out.println("Retrying handshake in 1.5s");
						Thread.sleep(1500);
						isSuccess = true;
					} catch(Exception e) {
						System.out.println("Error in handshake, at sleeping");
						e.printStackTrace();
					}
					continue;
				} else {
					option++;
				}
			}
		}
		
		return isSuccess && currentHandshakeAttempt < 3;
	}
	
	private void rollback(Host newSlave) {
		String alias = newSlave.getHostAddress();
		List<AgentTypeDTO> toDelete = new ArrayList<AgentTypeDTO>();
		
		agentsService.getAllSupportedAgentTypes().forEach(type -> {
			if(type.getAlias().equals(alias) || type.getHostAddress().equals(alias)) {
				toDelete.add(type);
			}
		});
		
		agentsService.getAllSupportedAgentTypes().removeAll(toDelete);	//Delete supported agents
		agentsService.getSlaveNodes().removeIf(x -> x.getHostAddress().equals(alias));	//Delete node
			
		agentsService.getSlaveNodes().forEach(slave -> {
			requestSender.deleteBadNode(slave.getHostAddress(), alias);
		});
	}

	private boolean sendRunningAgentsToNewSlave(String hostAddress, List<AID> allRunningAgents) {
		return requestSender.sendAllRunningAgentsToNewSlave(hostAddress, allRunningAgents);
	}


	private boolean sendAgentTypesToNewSlave(String hostAddress, List<AgentTypeDTO> allSupportedAgentTypes) {
		return requestSender.sendAgentTypesToNewSlave(hostAddress, allSupportedAgentTypes);
	}


	private boolean sendSlaveListToNewSlave(String hostAddress) {
		return requestSender.sendExistingSlavesToNewSlave(hostAddress, this.agentsService.getSlaveNodes());
	}


	public boolean sendRegisteredSlaveToSlaves(Host newSlave) {
		boolean isSuccess = true;
		
		if(agentsService.getNodeType().equals(NodeType.MASTER)) {
			for(Host slave : agentsService.getSlaveNodes()) {
				isSuccess = requestSender.registerSlaveNode(slave.getHostAddress(), newSlave) != null;
				if(!isSuccess) {
					break;
				}
			}	
		}
		
		return isSuccess;
	}
	
	public List<AgentType> fetchAgentTypeList() {
		try {
			return agentsService.getMySupportedAgentTypes();
		} catch(Exception e) {
			return null;
		}
	}
	
	public boolean sendNewAgentTypesToAllSlaves(List<AgentTypeDTO> newTypes) {
		boolean success = agentsService.getNodeType().equals(NodeType.MASTER);
		
		if(success) {
			List<AgentType> toSend = new ArrayList<AgentType>();
			newTypes.stream().forEach(t -> toSend.add(new AgentType(t.getName(), t.getModule())));
			
			for(Host slave : agentsService.getSlaveNodes()) {
				success = requestSender.sendNewAgentTypesToSlave(slave.getHostAddress(), toSend);
				if(!success) {
					System.err.println("Error at sending agent types to slave with host: " + slave.getHostAddress());
					break;
				}
			}	
		}
		
		return success;
	}
	
	public boolean addNewAgentTypes(List<AgentTypeDTO> agentTypes) throws JsonProcessingException, IOException {
		List<AgentTypeDTO> ret = agentsService.addNewAgentTypes(agentTypes);
		
		return ret != null;
	}
	
	public ArrayList<AgentInterface> sendRunningAgents(String myHostAddress){
		
		ArrayList<AgentInterface> retList = new ArrayList<AgentInterface>();
				
		//add my running agents
		for (Iterator<AgentInterface> i = agentsService.getMyRunningAgents().iterator(); i.hasNext();)
				retList.add(i.next());

		//only sending to other slaves (if I am the main node I will skip the slave who initiated the call)
		for (Iterator<Host> h = agentsService.getSlaveNodes().iterator(); h.hasNext();) {
			Host item = h.next();
				if(!item.getHostAddress().equals(myHostAddress)) {
					Response resp = requestSender.getRunningAgents(item.getHostAddress());
					ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
					for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
							retList.add(ra.next());
				}
		}
				
		//i am a slave node, send also to the main node
		if(agentsService.getNodeType().equals(NodeType.SLAVE)) {
			Host main = agentsService.getMainNode();
			Response resp = requestSender.getRunningAgents(main.getHostAddress());
			ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
			for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
				retList.add(ra.next());
			}
				
		return retList;							
	}
	
	public boolean deleteNode(String alias){
		boolean success = true;
		
		try {
			success = agentsService.deleteNode(alias);
		} catch(Exception e) {
			success = false;
			e.printStackTrace();
			System.out.println("Error deleting node. :(");
		}
		
		return success;
	}
	
}
