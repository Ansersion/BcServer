/**
 * 
 */
package other;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hub
 *
 */
public class BPError {
	// 0: error
	// else: no error
	
	public static final int BP_OK = 0;
	/* There are statistics unsupported signals existing.
	 * That means we should get the real signal values from the device manually.
	 * */
	public static final int BP_ERROR_STATISTICS_NONE_SIGNAL = 0xE0;
	
	private int errorCode;
	private int sigId;
	private List<Integer> statisticsNoneSignalList;
	
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

	public List<Integer> getStatisticsNoneSignalList() {
		return statisticsNoneSignalList;
	}

	public void setStatisticsNoneSignalList(List<Integer> statisticsNoneSignalList) {
		this.statisticsNoneSignalList = statisticsNoneSignalList;
	}
	
	public void putStatisticsNoneSignalId(int signalId) {
		if(null == statisticsNoneSignalList) {
			statisticsNoneSignalList = new ArrayList<>();
		}
		statisticsNoneSignalList.add(signalId);
	}
	
	
	
	
	
}
