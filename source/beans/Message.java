package beans;

import java.io.Serializable;

public class Message implements Serializable {
	
	private String messageType;
	private String content;
	
	public Message() {}
	
	public Message(String messageType, String content) {
		this.messageType = messageType;
		this.content = content;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [messageType=" + messageType + ", content=" + content + "]";
	}

}
