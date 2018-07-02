package beans;

import interfaces.AgentInterface;

public class PongAgent extends AgentClass implements AgentInterface{

	private static final long serialVersionUID = 1L;
	
	private AID aid;
		
	public PongAgent(AID aid) {
		super();
		this.aid = aid;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
}
