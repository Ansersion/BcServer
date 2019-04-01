/**
 * 
 */
package bc_console;

import bp_packet.BPPacket;

/**
 * @author Ansersion
 *
 */
public class BcConsoleCommandOpenRegister extends BcConsoleCommand {
	/* example: register open[close]*/
	public static final String CMD_WORD = "register";
	
	private boolean enableOpenRegister;

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
		String ret = "OK";
		BPPacket.setOpenRegister(enableOpenRegister);
		return ret;
	}

	public boolean isEnableOpenRegister() {
		return enableOpenRegister;
	}

	public void setEnableOpenRegister(boolean enableOpenRegister) {
		this.enableOpenRegister = enableOpenRegister;
	}
	
	

}
