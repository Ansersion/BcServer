package sys_sig_table;

import java.util.Map;


public class SysSigInfo {
	private boolean alm;
	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
	private byte valType;
	private int unitLangRes;
	/* 0-ro, 1-rw */
	private byte permission;
	private boolean ifDisplay;
	private byte accuracy;
	private Object valMin;
	private Object valMax;
	private Object valDef;
	private int classLangRes;
	private Map<Integer, Integer> mapEnmLangRes;
	private boolean enStatistics;
	// 0-note, 1-warning, 2-serious, 3-emergency
	private byte almClass;
	private int dlyBefAlm;
	private int dlyAftAlm;

	public SysSigInfo(boolean alm, byte valType, int unitLangRes,
			byte permission, boolean ifDisplay, byte accuracy, Object valMin, Object valMax,
			Object valDef, int classLangRes, Map<Integer, Integer> mapEnumLangRes,
			boolean enStatistics, byte almClass, int dlyBefAlm,
			int dlyAftAlm) {

		this.alm = alm;
		this.valType = valType;
		this.unitLangRes = unitLangRes;
		this.permission = permission;
		this.ifDisplay = ifDisplay;
		this.accuracy = accuracy;
		this.valMin = valMin;
		this.valMax = valMax;
		this.valDef = valDef;
		this.classLangRes = classLangRes;
		this.mapEnmLangRes = mapEnumLangRes;
		this.enStatistics = enStatistics;
		this.almClass = almClass;
		this.dlyBefAlm = dlyBefAlm;
		this.dlyAftAlm = dlyAftAlm;
	}

	public boolean isAlm() {
		return alm;
	}

	public void setAlm(boolean alm) {
		this.alm = alm;
	}

	public byte getValType() {
		return valType;
	}

	public void setValType(byte valType) {
		this.valType = valType;
	}

	public int getUnitLangRes() {
		return unitLangRes;
	}

	public void setUnitLangRes(int unitLangRes) {
		this.unitLangRes = unitLangRes;
	}

	public byte getPermission() {
		return permission;
	}

	public void setPermission(byte permission) {
		this.permission = permission;
	}

	public boolean isIfDisplay() {
		return ifDisplay;
	}

	public void setIfDisplay(boolean ifDisplay) {
		this.ifDisplay = ifDisplay;
	}

	public byte getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(byte accuracy) {
		this.accuracy = accuracy;
	}

	public Object getValMin() {
		return valMin;
	}

	public void setValMin(Object valMin) {
		this.valMin = valMin;
	}

	public Object getValMax() {
		return valMax;
	}

	public void setValMax(Object valMax) {
		this.valMax = valMax;
	}

	public Object getValDef() {
		return valDef;
	}

	public void setValDef(Object valDef) {
		this.valDef = valDef;
	}

	public int getClassLangRes() {
		return classLangRes;
	}

	public void setClassLangRes(int classLangRes) {
		this.classLangRes = classLangRes;
	}

	public Map<Integer, Integer> getMapEnmLangRes() {
		return mapEnmLangRes;
	}

	public void setMapEnmLangRes(Map<Integer, Integer> mapEnmLangRes) {
		this.mapEnmLangRes = mapEnmLangRes;
	}

	public boolean isEnStatistics() {
		return enStatistics;
	}

	public void setEnStatistics(boolean enStatistics) {
		this.enStatistics = enStatistics;
	}

	public byte getAlmClass() {
		return almClass;
	}

	public void setAlmClass(byte almClass) {
		this.almClass = almClass;
	}

	public int getDlyBefAlm() {
		return dlyBefAlm;
	}

	public void setDlyBefAlm(int dlyBefAlm) {
		this.dlyBefAlm = dlyBefAlm;
	}

	public int getDlyAftAlm() {
		return dlyAftAlm;
	}

	public void setDlyAftAlm(int dlyAftAlm) {
		this.dlyAftAlm = dlyAftAlm;
	}
	
	

	// public static
}
