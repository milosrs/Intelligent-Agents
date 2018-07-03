package config;

import services.AgentsService;

public class HeartbeatInvoker implements Runnable {

	private AgentsService agentsService;
	
	public void init (AgentsService as) {
		this.agentsService = as;
	}
	
	@Override
	public void run() {
		agentsService.checkSlavesHealth();
	}
}
