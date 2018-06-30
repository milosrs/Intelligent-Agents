package services;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import beans.AgentType;
import interfaces.AgentInterface;

public class JndiTreeParser {

	private String EXP = "java:global/";
	private String INTF = "!" + AgentInterface.class.getName();
	private Context context;
	
	public JndiTreeParser() {
		
		Hashtable<String, Object> env = new Hashtable<>();
		env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		  
		 try {
			this.context = new InitialContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<AgentType> parse() throws NamingException {
		  List<AgentType> result = new ArrayList<>();
		        
		  NamingEnumeration<NameClassPair> moduleList = context.list(EXP);
		  while (moduleList.hasMore()) {
		    String module = moduleList.next().getName();
		    processModule(module, result);
		  }
		  return result;
		}

	private void processModule(String module, List<AgentType> result) throws NamingException {
		  NamingEnumeration<NameClassPair> agentList = context.list(EXP + "/" + module);
		  while (agentList.hasMore()) {
		    String ejbName = agentList.next().getName();
		    AgentType agentType = parseEjbNameIfValid(module, ejbName);
		    if (agentType != null) {
		      result.add(agentType);
		    }
		  }
		}

	private AgentType parseEjbNameIfValid(String module, String ejbName) {
		  if (ejbName != null && ejbName.endsWith(INTF)) {
		    return parseEjbName(module, ejbName);
		  }
		  return null;
		}

	private AgentType parseEjbName(String module, String ejbName) {
		  ejbName = extractAgentName(ejbName);
		  	   
		  return new AgentType(ejbName,module);
		}

	private String extractAgentName(String ejbName) {
		  int n = ejbName.lastIndexOf(INTF);
		  return ejbName.substring(0, n);
		}

	
}
