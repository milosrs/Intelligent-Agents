package beans;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public abstract class AgentClass {

	private AID aid;
	
	@Inject
	public AgentClass (AID aid) {
		this.aid = aid;
	}
	
	public AgentClass () {
		this.aid = null;
	}
}
