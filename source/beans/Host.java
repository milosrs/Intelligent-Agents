package beans;

import java.io.Serializable;

public class Host implements Serializable {

	private static final long serialVersionUID = 1L;
	private String hostAddress;
	private String alias;
	
	public Host() {
		hostAddress = "";
		alias = "";
	}

	public Host(String hostAddress, String alias) {
		this.hostAddress = hostAddress;
		this.alias = alias;
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
}
