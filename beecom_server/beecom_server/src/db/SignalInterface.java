package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public abstract class SignalInterface {
	
	public Short getPermission() {
		return BPPacket.SIGNAL_PERMISSION_CODE_RO;
	}
	
	public Long getSystemSignalId() {
		return 0L;
	}
	public void setSystemSignalId(Long systemSignalId) {
	}
	
	public void setPermission(Short permission) {
	}
	
	public Boolean getEnStatistics() {
		return false;
	}
	public void setEnStatistics(Boolean enStatistics) {
	}
	
	public abstract Long getId();

	public abstract int getValType();

	public abstract long saveToDb(Session session);
	
	public abstract Object getDefaultValue();
	
	public void setCustomSignalId(Long customSignalId) {
		
	}
	
	public Integer getGroupLangId() {
		return 0;
	}
	
	public Short getAccuracy() {
		return 0;
	}
}
