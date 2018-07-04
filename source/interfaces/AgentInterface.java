package interfaces;

import java.io.Serializable;

import beans.ACLMessage;

public interface AgentInterface extends Serializable {

	public void handleMessage(ACLMessage message);
}
