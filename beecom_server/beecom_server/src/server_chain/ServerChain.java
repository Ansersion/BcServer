package server_chain;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerChain {
	
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_IPv4 = 1;
	public static final int TYPE_IPv6 = 2;
	public static final int TYPE_DOMAIN = 3;
	
	public static final ServerNode SERVER_NODE_DEFAULT = new ServerNode(TYPE_DEFAULT, "");
	
	// TODO: make SERVER_ROOT and SERVER_ME changeable
	public static final String SERVER_ROOT = "127.0.0.1";
	public static final int TYPE_ROOT = 1;
	public static final String SERVER_ME = SERVER_ROOT;
	public static final int TYPE_ME = TYPE_ROOT;
	
	/* example:
	 * 1<192.168.1.2>3<bcserver2.com */
	private String serverChainStr;
	private List<ServerNode> serverNodeList;

	public ServerChain(String serverChainStr) {
		super();
		serverNodeList = new ArrayList<ServerNode>();
		if(null == serverChainStr) {
			serverChainStr = "";
		}
		this.serverChainStr = serverChainStr;
		update(this.serverChainStr);
	}
	
	public void update(String serverChainStr) {
		serverNodeList.clear();
		if(serverChainStr.isEmpty()) {
			serverNodeList.add(SERVER_NODE_DEFAULT);
			return;
		}
		
		String[] serverArray = this.serverChainStr.split(">");
		if(null == serverArray || 0 == serverArray.length) {
			serverNodeList.add(SERVER_NODE_DEFAULT);
			return;
		}
		for(int i = 0; i < serverArray.length; i++) {
			String[] nodeArray = serverArray[i].split("<");
			/* 2 fields: type and address */
			if(2 != nodeArray.length) {
				continue;
			}
			serverNodeList.add(new ServerNode(parseInt(nodeArray[0]), nodeArray[1]));
		}
		if(serverNodeList.isEmpty()) {
			/* if no server node parsed, set it default */
			serverNodeList.add(SERVER_NODE_DEFAULT);
		}
	}
	
	public ServerNode getDirectNode() {
		return serverNodeList.get(0);
	}
	
	public ServerNode getUpperNode(ServerNode currentNode) {
		ServerNode ret = SERVER_NODE_DEFAULT;
		int size = serverNodeList.size() - 1;
		ServerNode serverNode;
		for(int i = 0; i < size; i++) {
			serverNode = serverNodeList.get(i);
			if(serverNode.getType() == currentNode.getType() && 0 == serverNode.getAddress().compareTo(currentNode.getAddress())) {
				ret = serverNodeList.get(i+1);
				break;
			}
		}
		
		return ret;
	}
	
	public ServerNode getLowerNode(ServerNode currentNode) {
		ServerNode ret = SERVER_NODE_DEFAULT;
		int size = serverNodeList.size() - 1;
		ServerNode serverNode;
		for(int i = size; i > 0; i--) {
			serverNode = serverNodeList.get(i);
			if(serverNode.getType() == currentNode.getType() && 0 == serverNode.getAddress().compareTo(currentNode.getAddress())) {
				ret = serverNodeList.get(i-1);
				break;
			}
		}
		
		return ret;
	}
	
	
    /* parse string to integer
     * @return 0 when error occurred or the integer parsed*/
    public static int parseInt(String s) {
    	int ret = 0;
    	if(null == s || s.isEmpty()) {
    		return ret;
    	}
    	try {
    		ret = Integer.parseInt(s);
    	} catch(Exception e) {
    		ret = 0;
    	}
    	
    	return ret;
    }
    
    /* parse string(hex) to integer
     * @return 0 when error occurred or the integer parsed*/
    public static int parseIntHex(String s) {
    	int ret = 0;
    	if(null == s || s.isEmpty()) {
    		return ret;
    	}
    	try {
    		ret = Integer.parseInt(s, 16);
    	} catch(Exception e) {
    		ret = 0;
    	}
    	
    	return ret;
    }
}
