package db;

public class SystemSignalCustomInfoUnit {
	
	private int sysSigId;
	private SignalInterface signalInterface;

	public SystemSignalCustomInfoUnit(int sysSigId, SignalInterface signalInterface) {
		super();
		this.sysSigId = sysSigId;
		this.signalInterface = signalInterface;
	}
	
	
	public int getSysSigId() {
		return sysSigId;
	}

	public void setSysSigId(int sysSigId) {
		this.sysSigId = sysSigId;
	}

	public SignalInterface getSignalInterface() {
		return signalInterface;
	}

	public void setSignalInterface(SignalInterface signalInterface) {
		this.signalInterface = signalInterface;
	}


	@Override
	public String toString() {
		return "SystemSignalCustomInfoUnit [sysSigId=" + sysSigId + ", signalInterface=" + signalInterface + "]";
	}
	
}
