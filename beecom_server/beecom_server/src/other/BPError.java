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
	private int errId;
	private List<Integer> sigIdLst;
	
	public BPError() {
		errId = 0;
		sigIdLst = null;
	}
	
	public void setErrId(int id) {
		errId = id;
	}
	
	public int getErrId() {
		return errId;
	}
	
	public boolean isErr() {
		return 0 != errId;
	}
	
	public void reset() {
		errId = 0;
		sigIdLst = null;
	}

	public List<Integer> getSigIdLst() {
		return sigIdLst;
	}

	public void setSigIdLst(List<Integer> sigIdLst) {
		this.sigIdLst = sigIdLst;
	}

	
}
