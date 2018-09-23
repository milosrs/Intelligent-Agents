package factories;

import javax.inject.Inject;

import beans.AID;
import beans.AgentClass;
import services.AgentsService;
import services.appConfigServices.JMSTopic;

public class AgentsFactory {
	
	public static AgentClass createAgent(AID aid, JMSTopic topic, AgentsService agentService) {

		AgentClass retObj;
		try {
			retObj = (AgentClass) Class.forName("beans." + aid.getType().getName()).newInstance();
			retObj.setAid(aid);
			retObj.init(topic,agentService);
		} catch (Exception e) {
			retObj = null;
		}
		
		return retObj;
	}
}
