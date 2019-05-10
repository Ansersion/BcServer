/**
 * 
 */
package bc_console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_server.BcDecoder;
import other.Util;
import server_chain.ServerChain;
import server_chain.ServerNode;

/**
 * @author Ansersion
 *
 */
public abstract class BcConsoleCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 
	
	public abstract String getCommandKey();
	public abstract boolean doCommand(BcConsoleCommandPara bcConsoleCommandPara);
	public abstract String doCommand();
	
	public static BcConsoleCommand createConsoleCommand(String[] array) {
		BcConsoleCommand ret = null;
		if(null == array || 0 == array.length) {
			return ret;
		}
		try {
			if (0 == array[0].trim().compareTo(BcConsoleCommandPrint.CMD_WORD)) {
				ret = new BcConsoleCommandPrint();
			} else if (0 == array[0].trim().compareTo(BcConsoleCommandAttach.CMD_WORD)) {
				BcConsoleCommandAttach tmp = new BcConsoleCommandAttach();
				if (array.length > 3) {
					ServerNode serverNode = new ServerNode(ServerChain.parseInt(array[1].trim()), array[2].trim());
					tmp.setServerNode(serverNode);
				} else {
					tmp = null;
				}
				ret = tmp;
			} else if(0 == array[0].trim().compareTo(BcConsoleCommandAdopt.CMD_WORD)) {
				BcConsoleCommandAdopt tmp = new BcConsoleCommandAdopt();
				if (array.length > 3) {
					ServerNode serverNode = new ServerNode(ServerChain.parseInt(array[1].trim()), array[2].trim());
					tmp.setServerNode(serverNode);
				} else {
					tmp = null;
				}
				ret = tmp;
			} else if(0 == array[0].trim().compareTo(BcConsoleCommandOpenRegister.CMD_WORD)) {
				BcConsoleCommandOpenRegister tmp = new BcConsoleCommandOpenRegister();
				if (array.length > 2) {
					if(0 == array[1].trim().compareTo("close")) {
						tmp.setEnableOpenRegister(false);
					} else if(0 == array[1].trim().compareTo("open")) {
						tmp.setEnableOpenRegister(true);
					} else {
						tmp = null;
					}
				} else {
					tmp = null;
				}
				ret = tmp;
			} else if(0 == array[0].trim().compareTo(BcConsoleCommandPayload.CMD_WORD)) {
				BcConsoleCommandPayload tmp = new BcConsoleCommandPayload();
				if (array.length > 2) {
					int payloadTmp = BcConsole.maxDeviceClientPayload;
					try {
						payloadTmp = Integer.valueOf(array[1]);
					} catch(Exception e) {
						Util.logger(logger, Util.ERROR, e);
					}
					tmp.setPayload(payloadTmp);
				} else {
					tmp = null;
				}
				ret = tmp;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}
}
