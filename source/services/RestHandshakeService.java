package services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import beans.AgentType;
import beans.AgentTypeDTO;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;
import requestSenders.RestHandshakeRequestSender;

@Stateful
public class RestHandshakeService {
	
	@Inject
	private RestHandshakeRequestSender requestSender;
	
	@Inject
	private AgentsService agentsService;
	
	@Inject
	private JndiTreeParser treeParser;

	public List<Host> registerSlaveNode(Host newSlave) {
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
			if(agentsService.getNodeType().equals(NodeType.MASTER)) {
				isSuccess = sendRegisteredSlaveToSlaves(newSlave);
			}
			if(isSuccess) {
				agentsService.getSlaveNodes().add(newSlave);	
			} else {
				return null;
			}
		}
		
		return agentsService.getSlaveNodes();
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
			return treeParser.parse();	
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
