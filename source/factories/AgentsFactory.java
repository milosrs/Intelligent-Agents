package factories;

import javax.inject.Inject;

import beans.AID;
import beans.AgentClass;
import jms.JMSTopic;
import services.AgentsService;

public class AgentsFactory {
	
	public static AgentClass createAgent(AID aid, JMSTopic topic, AgentsService agentService) {

		AgentClass retObj;
		try {
			retObj = (AgentClass) Class.forName("beans." + aid.getType().getName()).newInstance();
			retObj.setAid(aid);
			retObj.init(topic,agentService);
		} catch (Exception e) {
			System.out.println("Invalid Agent Type!");
			retObj = null;
		}
		
		return retObj;
	}
}
