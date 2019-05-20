/**
 * 
 */
package unit_test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import db.BeecomDB;
import db.DevServerChainHbn;
import server_chain.ServerChain;
import server_chain.ServerNode;

/**
 * @author Ansersion
 *
 */
public class ServerChainTest {
	
	
	private static final String TEST_SERVER_CHAIN_1 = "1<192.168.2.2>1<192.168.2.3";
	private static final String TEST_SERVER_CHAIN_2 = "";
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		BeecomDB.getInstance();
	}

	@Test
	public void test() {
		ServerChain serverChain = new ServerChain(TEST_SERVER_CHAIN_1);
		ServerNode tmp;
		tmp = serverChain.getDirectNode();
		assertEquals(tmp.getType(), ServerChain.TYPE_IPV4);
		assertEquals(0, tmp.getAddress().compareTo("192.168.2.2"));
		
		tmp = serverChain.getUpperNode(tmp);
		assertEquals(tmp.getType(), ServerChain.TYPE_IPV4);
		assertEquals(0, tmp.getAddress().compareTo("192.168.2.3"));
		
		tmp = serverChain.getLowerNode(tmp);
		assertEquals(tmp.getType(), ServerChain.TYPE_IPV4);
		assertEquals(0, tmp.getAddress().compareTo("192.168.2.2"));
		
		serverChain.update(TEST_SERVER_CHAIN_2);
		tmp = serverChain.getDirectNode();
		assertEquals(tmp.getType(), ServerChain.TYPE_DEFAULT);
		assertEquals(0, tmp.getAddress().compareTo(""));
		
		tmp = serverChain.getUpperNode(tmp);
		assertEquals(tmp.getType(), ServerChain.TYPE_DEFAULT);
		assertEquals(0, tmp.getAddress().compareTo(""));
		
		tmp = serverChain.getLowerNode(tmp);
		assertEquals(tmp.getType(), ServerChain.TYPE_DEFAULT);
		assertEquals(0, tmp.getAddress().compareTo(""));
		
		DevServerChainHbn serverChainHbn = BeecomDB.getInstance().getServerChain(1);
		assertEquals(serverChainHbn.getUpperServerType(), ServerChain.TYPE_DEFAULT);
		assertEquals(0, serverChainHbn.getUpperServer().compareTo(""));
		assertEquals(serverChainHbn.getLowerServerType(), ServerChain.TYPE_IPV4);
		assertEquals(0, serverChainHbn.getLowerServer().compareTo("192.168.1.2"));
	}

}
