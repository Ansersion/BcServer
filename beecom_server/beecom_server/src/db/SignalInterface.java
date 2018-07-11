package db;

import org.hibernate.Session;

public abstract class SignalInterface {
	
	private int bcSystemSignalId;
	
	public int getBcSystemSignalId() {
		return bcSystemSignalId;
	}

	public void setBcSystemSignalId(int bcSystemSignalId) {
		this.bcSystemSignalId = bcSystemSignalId;
	}

	public abstract int getValType();
	
	public abstract long saveToDb(Session session);
}
