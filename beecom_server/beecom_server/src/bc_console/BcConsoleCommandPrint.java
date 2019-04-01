/**
 * 
 */
package bc_console;

/**
 * command: print
 * @author Ansersion
 *
 */
public class BcConsoleCommandPrint extends BcConsoleCommand {
	/* example: print*/
	public static final String CMD_WORD = "print";

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
		
		ret = BcConsole.print();
		
		return ret;
	}

}
