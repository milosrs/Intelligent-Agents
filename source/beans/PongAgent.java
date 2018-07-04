package beans;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import interfaces.AgentInterface;

@Stateful
@Remote(AgentInterface.class)
public class PongAgent extends AgentClass {

	private static final long serialVersionUID = 1L;
	
	private AID aid;

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAid(AID aid) {
		this.aid = aid;
	}

	@Override
	public AID getAid() {
		return this.aid;
	}
}
