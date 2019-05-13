/**
 * 
 */
package bc_console;


/**
 * @author Ansersion
 *
 */
public class BcConsoleCommandPayload extends BcConsoleCommand {
	/* example: attach 1 127.0.0.1 */
	public static final String CMD_WORD = "payload";
	
	private int payload;
	

	/* (non-Javadoc)
	 * @see bc_console.BcConsoleCommand#getCommandKey()
	 */
	@Override
	public String getCommandKey() {
		return CMD_WORD;
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
		ret = BcConsole.updateDeviceClientPayload(payload);
		return ret;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}

	
}
