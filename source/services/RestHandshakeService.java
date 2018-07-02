package services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import beans.AgentType;
import beans.Host;
import beans.enums.NodeType;
import interfaces.AgentInterface;
import registrators.NodeRegistrator;
import requestSenders.RestHandshakeRequestSender;

@Stateful
public class RestHandshakeService {
	
	@Inject
	private RestHandshakeRequestSender requestSender;
	@Inject
	private NodeRegistrator nodeRegistrator;
	@Inject
	private JndiTreeParser treeParser;
	
	public List<Host> registerSlaveNode(Host newSlave) {
		boolean isSuccess = true;
		
		List<Host> slaves = nodeRegistrator.getSlaves();
			
		for (Host slave : slaves) {
			if (slave.getHostAddress().equals(newSlave.getHostAddress())
					|| slave.getAlias().equals(newSlave.getAlias())) {
				isSuccess = false;
				break;
			}
		}

		if (isSuccess) {
			if(nodeRegistrator.getNodeType().equals(NodeType.MASTER)) {
				isSuccess = sendRegisteredSlaveToSlaves(newSlave);
			}
			if(isSuccess) {
				nodeRegistrator.getSlaves().add(newSlave);	
			} else {
				return null;
			}
		}
		
		return nodeRegistrator.getSlaves();
	}
	
	
	public boolean sendRegisteredSlaveToSlaves(Host newSlave) {
		boolean isSuccess = true;
		
		if(nodeRegistrator.getNodeType().equals(NodeType.MASTER)) {
			for(Host slave : nodeRegistrator.getSlaves()) {
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
		boolean success = nodeRegistrator.getNodeType().equals(NodeType.MASTER);
		
		if(success) {
			for(Host slave : nodeRegistrator.getSlaves()) {
				success = requestSender.sendNewAgentTypesToSlave(slave.getHostAddress(), newTypes);
				if(!success) {
					System.err.println("Error at sending agent types to slave with host: " + slave.getHostAddress());
					break;
				}
			}	
		}
		
		return success;
	}
	
	public boolean addNewAgentTypes(List<AgentType> agentTypes) {
		List<AgentType> ret = nodeRegistrator.addNewAgentTypes(agentTypes);
		
		return ret == null;
	}
	
	public ArrayList<AgentInterface> sendRunningAgents(String myHostAddress){
		
		ArrayList<AgentInterface> retList = new ArrayList<AgentInterface>();
				
		//add my running agents
		for (Iterator<AgentInterface> i = nodeRegistrator.getRunningAgents().iterator(); i.hasNext();)
				retList.add(i.next());

		//only sending to other slaves (if I am the main node I will skip the slave who initiated the call)
		for (Iterator<Host> h = nodeRegistrator.getSlaves().iterator(); h.hasNext();) {
			Host item = h.next();
				if(!item.getHostAddress().equals(myHostAddress)) {
					Response resp = requestSender.getRunningAgents(item.getHostAddress());
					ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
					for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
							retList.add(ra.next());
				}
		}
				
		//i am a slave node, send also to the main node
		if(!nodeRegistrator.getMaster().getHostAddress().equals("ME")) {
			Host main = nodeRegistrator.getMaster();
			Response resp = requestSender.getRunningAgents(main.getHostAddress());
			ArrayList<AgentInterface> respAgents = resp.readEntity(new GenericType<ArrayList<AgentInterface>>() {});
			for(Iterator<AgentInterface> ra = respAgents.iterator(); ra.hasNext();)
				retList.add(ra.next());
			}
				
		return retList;							
	}
	
	public boolean deleteNode(String alias, ArrayList<AgentType> agentsToDelete){
		
		boolean retVal = true;
				
		boolean hasDeleted = nodeRegistrator.getSlaves().removeIf(x -> x.getAlias().equals(alias));
		boolean hasRemoved = true;
		if(!agentsToDelete.isEmpty())
			hasRemoved = nodeRegistrator.getSupportedAgentTypes().remove(agentsToDelete);
			
		if(!hasDeleted || !hasRemoved)
			retVal = false;
			
		return retVal;
	}
	
}
