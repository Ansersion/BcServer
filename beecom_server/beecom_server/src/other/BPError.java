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
	public int ErrId;
	public List<Integer> SigIdLst;
	
	public BPError() {
		ErrId = 0;
		SigIdLst = null;
	}
	
	public void setErrId(int id) {
		ErrId = id;
	}
	
	public int getErrId() {
		return ErrId;
	}
	
	public boolean isErr() {
		return 0 != ErrId;
	}
	
	public void reset() {
		ErrId = 0;
		SigIdLst = null;
	}
	
	public List<Integer> getLst() {
		return SigIdLst;
	}

}
