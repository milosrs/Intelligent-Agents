package factories;

import beans.AID;
import beans.AgentType;
import beans.ContractnetAgent;
import beans.Host;
import beans.PingAgent;
import beans.PongAgent;
import interfaces.AgentInterface;

public class AgentsFactory {

	public static AgentInterface createAgent(String name, Host host, AgentType type) {
		AID aid = new AID(name, host, type);
		if(type.getName().equals("PONG")) {
			return new PongAgent(aid);
		}
		else if(type.getName().equals("PING")) {
			return new PingAgent(aid);
		}
		else if(type.getName().equals("CONTRACTNET")) {
			return new ContractnetAgent(aid);
		}
		else
			return null;
	}
}
