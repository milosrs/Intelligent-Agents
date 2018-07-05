package interfaces;

import java.io.Serializable;

import beans.ACLMessage;
import jms.JMSTopic;

public interface AgentInterface extends Serializable {

	public void handleMessage(ACLMessage message);

}
