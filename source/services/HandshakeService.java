package services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import beans.AID;
import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;
import requestSenders.HandshakeRequestSender;

@Stateful
public class HandshakeService {
	private final int handshakeAttempts = 3;
	@Inject
	private HandshakeRequestSender requestSender;
	@Inject
	private AgentsService agentsService;
	@Inject
	private JndiTreeParser treeParser;
	private List<AgentType> supported = null;

	public List<Host> startHandshake(Host newSlave) {
		boolean isSuccess = true;
		
		List<Host> slaves = agentsService.getSlaveNodes();
			
		for (Host slave : slaves) {
			if (slave.getHostAddress().equals(newSlave.getHostAddress())
					|| slave.getAlias().equals(newSlave.getAlias())) {
				isSuccess = false;
				break;
			}
		}

		if (isSuccess) {
			int currentHandshakeAttempt = 0;
			int option = 0;
			
			if(agentsService.getNodeType().equals(NodeType.MASTER)) {
				while(currentHandshakeAttempt < 3) {
					switch(option) {
					case 0: supported = requestSender.fetchAgentTypeList(newSlave.getHostAddress()); break;
					case 1: isSuccess = sendRegisteredSlaveToSlaves(newSlave); break;
					case 2: isSuccess = sendNewAgentTypesToAllSlaves(supported);  break;
					case 3: isSuccess = sendSlaveListToNewSlave(newSlave.getHostAddress()); break;
					case 4: isSuccess = sendAgentTypesToNewSlave(newSlave.getHostAddress(), agentsService.getAllSupportedAgentTypes()); break;
					case 5: isSuccess = sendRunningAgentsToNewSlave(newSlave.getHostAddress(), agentsService.getAllRunningAgents()); break;
					default: System.out.println("Handshake successfull!"); break;
					}
					
					if(!isSuccess) {
						currentHandshakeAttempt++;
						try {
							Thread.sleep(1500);
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
			if(isSuccess) {
				agentsService.getSlaveNodes().add(newSlave);	
			} else {
//				rollback(option, newSlave);
				System.out.println("Should rollback!");
			}
		}
		
		return agentsService.getSlaveNodes();
	}
	
	
//	private void rollback(int option, Host newSlave) {
//		if(option > 2) {
//			option = 2;
//		}
//		
//		while(option > 0) {
//			switch(option) {
//			case 0: break;
//			case 1: deleteRegisteredSlaveFromSlaves(newSlave); break;
//			case 2: deleteNewAgentTypesFromAllSlaves(supported); break;
//			}
//			
//			option++;
//		}
//		
//	}

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
	
	public boolean sendNewAgentTypesToAllSlaves(List<AgentType> newTypes) {
		boolean success = agentsService.getNodeType().equals(NodeType.MASTER);
		
		if(success) {
			for(Host slave : agentsService.getSlaveNodes()) {
				success = requestSender.sendNewAgentTypesToSlave(slave.getHostAddress(), newTypes);
				if(!success) {
					System.err.println("Error at sending agent types to slave with host: " + slave.getHostAddress());
					break;
				}
			}	
		}
		
		return success;
	}
	
	public boolean addNewAgentTypes(List<AgentTypeDTO> agentTypes) {
		List<AgentTypeDTO> ret = agentsService.addNewAgentTypes(agentTypes);
		
		return ret == null;
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
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean deleteNode(String alias, ArrayList<AgentType> agentsToDelete){
		
		boolean retVal = true;
				
		boolean hasDeleted = agentsService.getSlaveNodes().removeIf(x -> x.getAlias().equals(alias));
		boolean hasRemoved = true;
		if(!agentsToDelete.isEmpty())
			hasRemoved = agentsService.getMySupportedAgentTypes().remove(agentsToDelete);
			
		if(!hasDeleted || !hasRemoved)
			retVal = false;
			
		return retVal;
	}
	
}
