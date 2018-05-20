package db;

public class SystemSignalInfoUnit {

	private int sysSigId;
	private boolean ifNotifing;
	private boolean ifConfigDef;
	private SystemSignalInterface systemSignalInterface;

	public SystemSignalInfoUnit(int sysSigId, boolean ifNotifing, boolean ifConfigDef,
			SystemSignalInterface systemSignalInterface) {
		super();
		this.sysSigId = sysSigId;
		this.ifNotifing = ifNotifing;
		this.ifConfigDef = ifConfigDef;
		this.systemSignalInterface = systemSignalInterface;
	}

	public int getSysSigId() {
		return sysSigId;
	}

	public boolean isIfNotifing() {
		return ifNotifing;
	}

	public boolean isIfConfigDef() {
		return ifConfigDef;
	}

	public SystemSignalInterface getSystemSignalInterface() {
		return systemSignalInterface;
	}

	@Override
	public String toString() {
		return "SystemSignalInfoUnit [sysSigId=" + sysSigId + ", ifNotifing=" + ifNotifing + ", ifConfigDef="
				+ ifConfigDef + ", systemSignalInterface=" + systemSignalInterface + "]";
	}

	
	
	
}
