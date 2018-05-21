package db;

public abstract class SignalInterface {
	
	private int bcSystemSignalId;
	
	public int getBcSystemSignalId() {
		return bcSystemSignalId;
	}

	public void setBcSystemSignalId(int bcSystemSignalId) {
		this.bcSystemSignalId = bcSystemSignalId;
	}

	public abstract int getValType();
}
