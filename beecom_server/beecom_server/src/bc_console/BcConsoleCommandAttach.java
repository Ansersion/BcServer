/**
 * 
 */
package bc_console;

import server_chain.ServerChain;
import server_chain.ServerNode;

/**
 * @author Ansersion
 *
 */
public class BcConsoleCommandAttach extends BcConsoleCommand {
	
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
		ret = BcConsole.updateServerFather(serverNode);
		return ret;
	}

}
