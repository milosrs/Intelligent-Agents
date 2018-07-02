package beans;

import javax.ejb.Stateless;

import interfaces.AgentInterface;

@Stateless
public class PingAgent extends AgentClass implements AgentInterface{

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	

}
