package beans;

import javax.ejb.Stateful;

@Stateful
public class PongAgent extends AgentClass {

	private static final long serialVersionUID = 1L;
	
	private AID aid;

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
}
