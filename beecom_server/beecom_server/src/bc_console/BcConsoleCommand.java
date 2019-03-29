/**
 * 
 */
package bc_console;

/**
 * @author Ansersion
 *
 */
public abstract class BcConsoleCommand {
	public abstract String getCommandKey();
	public abstract boolean doCommand(BcConsoleCommandPara bcConsoleCommandPara);
	public abstract String doCommand();
	
	public static BcConsoleCommand createConsoleCommand(String[] array) {
		return null;
	}
}
