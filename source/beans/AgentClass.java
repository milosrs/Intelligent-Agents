package beans;

import interfaces.AgentInterface;

public abstract class AgentClass implements AgentInterface {

	private static final long serialVersionUID = 1L;
	
	public abstract void setAid(AID aid);
	public abstract AID getAid();
}
