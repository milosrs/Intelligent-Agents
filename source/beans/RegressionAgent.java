package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import beans.enums.Performative;
import interfaces.AgentInterface;
import services.AgentsService;
import services.RegressionService;
import services.appConfigServices.JMSTopic;

@Stateful
@Remote(AgentInterface.class)
public class RegressionAgent extends AgentClass{

	private static final long serialVersionUID = 1L;
	
	private AID aid;
	private JMSTopic topic;
	private AgentsService agentsService;
	
	private RegressionService regressionService = new RegressionService();
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.REQUEST)) {
				
			if(message.getContentObj()!=null) {
				int matchRating = (int) message.getContentObj();
				ACLMessage aclMessage = new ACLMessage();
				
				ArrayList<AID> receivers = new ArrayList<AID>();
				receivers.add(message.getSender());
				
				aclMessage.setSender(this.aid);
				aclMessage.setPerformative(Performative.INFORM);
				aclMessage.setReceivers(receivers);
				
				HashMap<String,Object> userArgs = new HashMap<String,Object>();
				
				FootballPredictions footballPrediction = regressionService.getPredictions();
				
				regressionService.fit(footballPrediction.getMatchRatings(), footballPrediction.getHomeWinPercentage());				
				double homeWinnerPercentage = regressionService.predict(matchRating);
				
				regressionService.fit(footballPrediction.getMatchRatings(), footballPrediction.getTiePercentage());				
				double tiePercentage = regressionService.predict(matchRating);
				
				regressionService.fit(footballPrediction.getMatchRatings(), footballPrediction.getAwayWinPercentage());				
				double awayWinnerPercentage = regressionService.predict(matchRating);
				
				userArgs.put("homeWinnerPercentage", homeWinnerPercentage);
				userArgs.put("tiePercentage", tiePercentage);
				userArgs.put("awayWinnerPercentage", awayWinnerPercentage);
				
				aclMessage.setUserArgs(userArgs);
				
				sendSocketMessage(aclMessage);
				topic.send(aclMessage);
			}		
		}
		
	}

	@Override
	public void setAid(AID aid) {
		this.aid = aid;
	}

	@Override
	public AID getAid() {
		return this.aid;
	}

	@Override
	public void init(JMSTopic topic, AgentsService agentService) {
		this.topic = topic;
		this.agentsService = agentService;		
	}
	
}
