/**
 * 
 */
package db;

/**
 * @author Ansersion
 *
 */
public class DeviceReportRec {
	private long timestamp;
	private int reportSigtableNum;
	
	public DeviceReportRec(long timestamp, int reportSigtableNum) {
		super();
		this.timestamp = timestamp;
		this.reportSigtableNum = reportSigtableNum;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public void setReportSigtableNum(int reportSigtableNum) {
		this.reportSigtableNum = reportSigtableNum;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public int getReportSigtableNum() {
		return reportSigtableNum;
	}
	
	
}
