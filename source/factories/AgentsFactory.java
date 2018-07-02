package factories;

import beans.AID;
import beans.ContractnetAgent;
import beans.PingAgent;
import beans.PongAgent;
import interfaces.AgentInterface;

public class AgentsFactory {

	public static AgentInterface createAgent(AID aid) {
		if(aid.getType().getName().equals("PONG")) {
			return new PongAgent(aid);
		}
		else if(aid.getType().getName().equals("PING")) {
			PingAgent retObj = new PingAgent();
			retObj.init(aid);
			return retObj;
		}
		else if(aid.getType().getName().equals("CONTRACTNET")) {
			return new ContractnetAgent(aid);
		}
		else
			return null;
	}
}
