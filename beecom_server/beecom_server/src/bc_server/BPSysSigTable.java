/**
 * 
 */
package bc_server;

import java.util.Map;

/**
 * @author Ansersion
 * 
 */

public class BPSysSigTable {
	// int SigId;
	// byte[] MacroName;
	boolean IsAlm;
	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
	byte ValType;
	int UnitLangRes;
	/* 0-ro, 1-rw */
	byte Permission;
	byte Accuracy;
	BPValue ValMin;
	BPValue ValMax;
	BPValue ValDef;
	Map<Integer, Integer> MapEnmLangRes;
	boolean EnStatics;
	// 0-note, 1-warning, 2-serious, 3-emergency
	byte AlmClass;
	int DlyBefAlm;
	int DlyAftAlm;

	public BPSysSigTable(boolean is_alm, byte val_type, int unit_lang_res,
			byte permission, byte accuracy, BPValue val_min, BPValue val_max,
			BPValue val_def, Map<Integer, Integer> map_enm_lang_res,
			boolean en_statics, byte alm_class, int dly_bef_alm, int dly_aft_alm) {

		IsAlm = is_alm;
		ValType = val_type;
		UnitLangRes = unit_lang_res;
		Permission = permission;
		Accuracy = accuracy;
		ValMin = val_min;
		ValMax = val_max;
		ValDef = val_def;
		MapEnmLangRes = map_enm_lang_res;
		EnStatics = en_statics;
		AlmClass = alm_class;
		DlyBefAlm = dly_bef_alm;
		DlyAftAlm = dly_aft_alm;
	}
	
	//public static 
}
