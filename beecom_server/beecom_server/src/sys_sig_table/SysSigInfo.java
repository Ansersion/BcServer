package sys_sig_table;

import java.util.Map;

import other.BPValue;

public class SysSigInfo {
	// byte[] MacroName;
	public boolean IsAlm;
	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
	public byte ValType;
	public int UnitLangRes;
	/* 0-ro, 1-rw */
	public byte Permission;
	public byte Accuracy;
	public BPValue ValMin;
	public BPValue ValMax;
	public BPValue ValDef;
	public int classLangRes;
	public Map<Integer, Integer> MapEnmLangRes;
	public boolean EnStatistics;
	// 0-note, 1-warning, 2-serious, 3-emergency
	public byte AlmClass;
	public int DlyBefAlm;
	public int DlyAftAlm;

	public SysSigInfo(boolean is_alm, byte val_type, int unit_lang_res,
			byte permission, byte accuracy, BPValue val_min, BPValue val_max,
			BPValue val_def, int classLangRes, Map<Integer, Integer> map_enm_lang_res,
			boolean en_statistics, byte alm_class, int dly_bef_alm,
			int dly_aft_alm) {

		IsAlm = is_alm;
		ValType = val_type;
		UnitLangRes = unit_lang_res;
		Permission = permission;
		Accuracy = accuracy;
		ValMin = val_min;
		ValMax = val_max;
		ValDef = val_def;
		this.classLangRes = classLangRes;
		MapEnmLangRes = map_enm_lang_res;
		EnStatistics = en_statistics;
		AlmClass = alm_class;
		DlyBefAlm = dly_bef_alm;
		DlyAftAlm = dly_aft_alm;
	}

	// public static
}
