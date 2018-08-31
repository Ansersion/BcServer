package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public abstract class SignalInterface {
	
	private int bcSystemSignalId;
	
	public int getBcSystemSignalId() {
		return bcSystemSignalId;
	}

	public void setBcSystemSignalId(int bcSystemSignalId) {
		this.bcSystemSignalId = bcSystemSignalId;
	}
	
	public Short getPermission() {
		return BPPacket.SIGNAL_PERMISSION_CODE_RO;
	}
	
	public void setPermission(Short permission) {
	}
	
	public Boolean getEnStatistics() {
		return false;
	}
	public void setEnStatistics(Boolean enStatistics) {
	}

	public abstract int getValType();

	public abstract long saveToDb(Session session);
	
	public abstract Object getDefaultValue();
}
