package beans;

public class AgentTypeDTO {

	private AgentType type;
	
	private Host host;

	public AgentTypeDTO(AgentType type, Host host) {
		super();
		this.type = type;
		this.host = host;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}		
}
