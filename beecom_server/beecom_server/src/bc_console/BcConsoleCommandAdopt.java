/**
 * 
 */
package bc_console;

import server_chain.ServerNode;

/**
 * @author Ansersion
 *
 */
public class BcConsoleCommandAdopt extends BcConsoleCommand {
	/* example: adopt 1 127.0.0.1 */
	public static final String CMD_WORD = "adopt";
	
	private ServerNode serverNode;

	/* (non-Javadoc)
	 * @see bc_console.BcConsoleCommand#getCommandKey()
	 */
	@Override
	public String getCommandKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see bc_console.BcConsoleCommand#doCommand(bc_console.BcConsoleCommandPara)
	 */
	@Override
	public boolean doCommand(BcConsoleCommandPara bcConsoleCommandPara) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see bc_console.BcConsoleCommand#doCommand()
	 */
	@Override
	public String doCommand() {
		String ret;
		ret = BcConsole.adopt(serverNode);
		return ret;
	}

	public ServerNode getServerNode() {
		return serverNode;
	}

	public void setServerNode(ServerNode serverNode) {
		this.serverNode = serverNode;
	}
	
	

}
