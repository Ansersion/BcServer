/**
 * 
 */
package db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import bp_packet.BPParseCsvFileException;
import other.Util;

/**
 * @author Ansersion
 * 
 */
public class DBSysSigRec extends DBBaseRec {
	
	private static final Logger logger = LoggerFactory.getLogger(DBSysSigRec.class);

	int sysSigTabId;
	List<Byte[]> sysSigEnableLst;
	

	public DBSysSigRec() {
		sysSigTabId = 0;
		sysSigEnableLst = new ArrayList<>();
	}

	// NOTE: ensure the capacity of sysSigEnableLst bigger than DIST_NUM
	public DBSysSigRec(int sysSigTabId, List<Byte[]> sysSigEnableLst) {
		this.sysSigTabId = sysSigTabId;
		this.sysSigEnableLst = sysSigEnableLst;
		// SysSigEnableLst.ensu
	}


	public DBSysSigRec(int sysSigTabId, Byte[] sigBasic, Byte[] sigTemp,
			Byte[] sigClean) {
		this.sysSigTabId = sysSigTabId;
		sysSigEnableLst = new ArrayList<>();
		sysSigEnableLst.add(sigBasic);
		sysSigEnableLst.add(sigTemp);
		sysSigEnableLst.add(sigClean);
	}

	public DBSysSigRec(int sysSigTabId, byte[] sigBasic, byte[] sigTemp,
			byte[] sigClean) {
		this.sysSigTabId = sysSigTabId;

		if (null == sigBasic) {
			sysSigEnableLst.add(null);
		} else {
			sysSigEnableLst = new ArrayList<>();
			Byte[] tmp1 = new Byte[sigBasic.length];
			for (int i = 0; i < sigBasic.length; i++) {
				tmp1[i] = sigBasic[i];
			}
			sysSigEnableLst.add(tmp1);
		}

		if (null == sigTemp) {
			sysSigEnableLst.add(null);
		} else {
			Byte[] tmp2 = new Byte[sigTemp.length];
			for (int i = 0; i < sigTemp.length; i++) {
				tmp2[i] = sigTemp[i];
			}
			sysSigEnableLst.add(tmp2);
		}

		if (null == sigClean) {
			sysSigEnableLst.add(null);
		} else {
			Byte[] tmp3 = new Byte[sigClean.length];
			for (int i = 0; i < sigClean.length; i++) {
				tmp3[i] = sigClean[i];
			}
			sysSigEnableLst.add(tmp3);
		}
	}

	public int getSysSigTabId() {
		return sysSigTabId;
	}

	public List<Byte[]> getSysSigEnablesLst() {
		return sysSigEnableLst;
	}

	public void setSysSigTabId(long sysSigTabId) {
		this.sysSigTabId = (int) sysSigTabId;
		setDirty();
	}

	public void setSysSigEnableLst(List<Byte[]> sysSigEnableLst) {
		this.sysSigEnableLst = sysSigEnableLst;
		setDirty();
	}

	public void setSysSigEnableLst(Map<Integer, Byte[]> sysSigEnableMap) {
		Iterator<Map.Entry<Integer, Byte[]>> entries = sysSigEnableMap
				.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte[]> entry = entries.next();
			int dist = entry.getKey();
			while (dist > this.sysSigEnableLst.size() - 1) {
				this.sysSigEnableLst.add(new Byte[1]);
			}
			this.sysSigEnableLst.set(dist, entry.getValue());
			setDirty();
		}
	}

	public void dumpRec() {
		try {
			StringBuilder dumpSysSig = new StringBuilder();
			dumpSysSig.append("SysSigTabId: " + sysSigTabId + "\n");
			for (int i = 0; i < sysSigEnableLst.size(); i++) {
				Byte[] sysSig = sysSigEnableLst.get(i);
				if (null == sysSig) {
					continue;
				}
				for (int j = 0; j < sysSig.length; j++) {
					dumpSysSig.append(Integer.toHexString(sysSig[j])
							.toUpperCase() + " ");
				}
				dumpSysSig.append("\n");
			}
			logger.debug(dumpSysSig.toString());
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}

	@Override
	public boolean updateRec(Connection con) {
		return false;
	}

	public boolean insertRec(Connection con) {
		
		StringBuilder sqlKey = new StringBuilder();
		sqlKey.append("(sysSigTabId");
		StringBuilder sqlVal = new StringBuilder();
		sqlVal.append("values(" + sysSigTabId);
		for (int i = 0; i < sysSigEnableLst.size(); i++) {
			Byte[] sigMapOri = sysSigEnableLst.get(i);
			if (null != sigMapOri) {
				sqlKey.append("," + "ssd" + String.format("%02d", i));
				sqlVal.append("," + "?");
			} 
		}
		sqlKey.append(")");
		sqlVal.append(")");
		String sql = "insert into sysSigTab" + sqlKey + " " + sqlVal;

		
		try (PreparedStatement ps = con.prepareStatement(sql);) {
			if(sysSigEnableLst.size() > BPPacket.MAX_SYS_SIG_DIST_NUM) {
				throw new BPParseCsvFileException("SysSigEnableLst.size() > MAX_DIST_NUM");
			}
			
			for (int i = 0; i < sysSigEnableLst.size(); i++) {
				Byte[] sigMapOri = sysSigEnableLst.get(i);
				if (null != sigMapOri) {
					byte[] sigMap = new byte[sigMapOri.length];
					for (int j = 0; j < sigMap.length; j++) {
						sigMap[j] = sigMapOri[j];
					}
					ps.setBytes(i + 1, sigMap);
				} 
			}
			ps.executeUpdate();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}

		return false;
	}
}
