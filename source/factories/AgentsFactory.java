package factories;

import java.lang.reflect.Constructor;

import beans.AID;
import beans.AgentClass;

public class AgentsFactory {

	@SuppressWarnings("rawtypes")
	public static AgentClass createAgent(AID aid) {

		AgentClass retObj;
		try {
			Constructor c = Class.forName(aid.getType().getName()).getConstructor(AID.class);
			retObj = (AgentClass) c.newInstance(aid);
		} catch (Exception e) {
			System.out.println("Invalid Agent Type!");
			retObj = null;
		}
		
		return retObj;
	}
}
