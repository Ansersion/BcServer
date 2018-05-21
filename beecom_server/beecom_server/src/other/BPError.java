/**
 * 
 */
package other;

import java.util.List;

/**
 * @author hub
 *
 */
public class BPError {
	// 0: error
	// else: no error
	private int errorCode;
	private int sigId;
	
	public BPError() {
		super();
		errorCode = 0;
		sigId = 0;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getSigId() {
		return sigId;
	}

	public void setSigId(int sigId) {
		this.sigId = sigId;
	}
	
	
	
	
}
