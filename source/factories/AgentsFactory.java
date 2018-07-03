package factories;

import beans.AID;
import beans.AgentClass;

public class AgentsFactory {

	public static AgentClass createAgent(AID aid) {

		AgentClass retObj;
		try {
			retObj = (AgentClass) Class.forName("beans." + aid.getType().getName()).newInstance();
			retObj.setAid(aid);
		} catch (Exception e) {
			System.out.println("Invalid Agent Type!");
			retObj = null;
		}
		
		return retObj;
	}
}
