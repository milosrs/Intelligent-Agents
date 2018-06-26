package beans;

public class Host {

	private String hostAddress;
	private String alias;
	
	public Host() {
		hostAddress = "";
		alias = "";
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
