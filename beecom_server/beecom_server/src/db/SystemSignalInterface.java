package db;

public abstract class SystemSignalInterface {

	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string, 7-boolean */
	public static final int VAL_TYPE_UINT32 = 0;
	public static final int VAL_TYPE_UINT16 = 1;
	public static final int VAL_TYPE_IINT32 = 2;
	public static final int VAL_TYPE_IINT16 = 3;
	public static final int VAL_TYPE_ENUM = 4;
	public static final int VAL_TYPE_FLOAT = 5;
	public static final int VAL_TYPE_STRING = 6;
	public static final int VAL_TYPE_BOOLEAN = 7;
	public static final int VAL_TYPE_INVALID = 0x7F;
	
	private int systemSignalId;

	public int systemSignalId() {
		return systemSignalId;
	}
	
	public void setSystemSignalId(int systemSignalId) {
		this.systemSignalId = systemSignalId;
	}
	
	public abstract int getValType();
}
