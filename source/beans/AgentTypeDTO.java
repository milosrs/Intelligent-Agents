package beans;

import java.io.Serializable;

public class AgentTypeDTO implements Serializable {

	private String name;
	private String module;
	private String hostAddress;
	private String alias;
	
	public AgentTypeDTO() {
		super();
	}

	public AgentTypeDTO(String name, String module, String hostAddress, String alias) {
		super();
		this.name = name;
		this.module = module;
		this.hostAddress = hostAddress;
		this.alias = alias;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getHostAddress() {
		return hostAddress;
	}


	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}


	public String getAlias() {
		return alias;
	}


	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void convertToDTO(AgentType type, Host host) {
		this.name = type.getName();
		this.module = type.getModule();
		this.hostAddress = host.getHostAddress();
		this.alias = host.getAlias();
	}
}
