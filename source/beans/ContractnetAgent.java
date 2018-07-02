package beans;

import interfaces.AgentInterface;

public class ContractnetAgent implements AgentInterface {

	private static final long serialVersionUID = 1L;
	
	private AID aid;
		
	public ContractnetAgent(AID aid) {
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
