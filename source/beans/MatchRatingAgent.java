package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import beans.enums.Performative;
import interfaces.AgentInterface;
import jms.JMSTopic;
import services.AgentsService;
import services.ResultPredictionService;

@Stateful
@Remote(AgentInterface.class)
public class MatchRatingAgent extends AgentClass{

	private static final long serialVersionUID = 1L;
	private AID aid;
	private JMSTopic topic;
	private AgentsService agentsService;
	private int regressionAgents = 0;
	private double homeWinnerPercentage = 0.0;
	private double tiePercentage = 0.0;
	private double awayWinnerPercentage = 0.0;
	
	private ResultPredictionService resultPredictionService = new ResultPredictionService();
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.REQUEST)) {
			
			regressionAgents = 0;
			homeWinnerPercentage = 0.0;
			tiePercentage = 0.0;
			awayWinnerPercentage = 0.0;
			
			ACLMessage aclMessage = new ACLMessage();
			aclMessage.setSender(this.aid);
			if(message.getContent().isEmpty() || !message.getContent().contains("-")) {				
				aclMessage.setPerformative(Performative.FAILURE);			
				sendSocketMessage(aclMessage);
				return;
			}
			
			String[] splits = message.getContent().split("-");
			String homeTeam = splits[0].trim();
			String awayTeam = splits[1].trim();
			
			ArrayList<FootballResult> results = (ArrayList<FootballResult>) resultPredictionService.readResults();
			int matchRating = resultPredictionService.getMatchRating(results, homeTeam, awayTeam);
			
			ArrayList<AID> receivers = new ArrayList<AID>();
			
			for(AID receiver : agentsService.getAllRunningAgents()) {
				if(receiver.getType().getName().equals("RegressionAgent")) {
					receivers.add(receiver);
				}
			}
			
			if(receivers.isEmpty()) {
				aclMessage.setPerformative(Performative.CANCEL);
				sendSocketMessage(aclMessage);
			}else {			
				aclMessage.setPerformative(Performative.REQUEST);
				aclMessage.setContentObj(matchRating);
				
				aclMessage.setReceivers(receivers);
				
				sendSocketMessage(aclMessage);
				topic.send(aclMessage);
				
			}		
		}else if(message.getPerformative().equals(Performative.INFORM)) {
			
			regressionAgents++;
			homeWinnerPercentage = (homeWinnerPercentage + (double) message.getUserArgs().get("homeWinnerPercentage"))/regressionAgents;
			tiePercentage = (tiePercentage + (double) message.getUserArgs().get("tiePercentage"))/regressionAgents;
			awayWinnerPercentage = (awayWinnerPercentage + (double) message.getUserArgs().get("awayWinnerPercentage"))/regressionAgents;
			
			int realRegressionAgents = 0;
			
			for(AID receiver : agentsService.getAllRunningAgents()) {
				if(receiver.getType().getName().equals("RegressionAgent")) {
					realRegressionAgents++;
				}
			}
			
			if(regressionAgents>=realRegressionAgents) {
				
				ACLMessage aclMessage = new ACLMessage();
				aclMessage.setSender(this.aid);
				aclMessage.setPerformative(Performative.INFORM);
				
				HashMap<String,Object> userArgs = new HashMap<String,Object>();
				userArgs.put("homeWinnerPercentage", homeWinnerPercentage);
				userArgs.put("tiePercentage", tiePercentage);
				userArgs.put("awayWinnerPercentage", awayWinnerPercentage);
				
				aclMessage.setUserArgs(userArgs);
				sendSocketMessage(aclMessage);	
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
