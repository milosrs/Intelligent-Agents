package config;

import javax.ejb.Stateless;
import javax.inject.Inject;

import registrators.NodeRegistrator;

@Stateless
public class HeartbeatInvoker implements Runnable {

	@Inject
	private NodeRegistrator registrator;
	
	@Override
	public void run() {
		registrator.checkSlavesHealth();
	}
}
