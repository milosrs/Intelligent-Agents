package services;

import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;

import beans.AgentType;
import beans.Host;
import beans.enums.NodeType;
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
}
