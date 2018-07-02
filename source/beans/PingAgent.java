package beans;

import javax.ejb.Stateful;

import interfaces.AgentInterface;

@Stateful
public class PingAgent extends AgentClass implements AgentInterface {
	
	private static final long serialVersionUID = 1L;
	
	private AID aid;
	
	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
	
	public void init(AID aid) {
		this.aid = aid;
	}
}
