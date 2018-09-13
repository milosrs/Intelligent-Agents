package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Response;

import org.jboss.as.cli.CommandLineException;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/websocket")
public class WebSocketController {

	public static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
    public void helloOnOpen(Session session) {
		sessions.add(session);
        System.out.println("WebSocket opened: " + session.getId());

    }

    @OnClose
    public void helloOnClose(Session session, CloseReason reason) {
    	sessions.remove(session);
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }
    
    /*@OnMessage
    public String sayHello(String message, Session session) throws JsonParseException, JsonMappingException, IOException, ParseException, InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException, CommandLineException {
    	
    	ObjectMapper mapper = new ObjectMapper();
		Message clientMessage = mapper.readValue(message, Message.class);

		if(clientMessage != null) {
			String content = clientMessage.getContent();
			String loggedUserName = clientMessage.getLoggedUserName();

			switch (clientMessage.getMessageType()) {
			}
    	
    	}
    }
    */
    
}
