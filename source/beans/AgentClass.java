package beans;

import javax.ejb.Stateful;
import javax.inject.Inject;

import interfaces.AgentInterface;

@Stateful
public abstract class AgentClass implements AgentInterface {

	private static final long serialVersionUID = 1L;
	private AID aid;
	
	@Inject
	public AgentClass (AID aid) {
		this.aid = aid;
	}
	
	public AgentClass () {
		this.aid = null;
	}
}
