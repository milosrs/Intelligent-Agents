package interfaces;

import java.io.Serializable;

import beans.ACLMessage;
import services.appConfigServices.JMSTopic;

public interface AgentInterface extends Serializable {

	public void handleMessage(ACLMessage message);

}
