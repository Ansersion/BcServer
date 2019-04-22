package db;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import other.Util;

public class CustomSignalI16InfoHbn extends SignalInterface {
	private static final Logger logger = LoggerFactory.getLogger(CustomSignalI16InfoHbn.class); 
    private Long id;
    private Short permission;
    private Short minVal;
    private Short maxVal;
    private Short defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
	public CustomSignalI16InfoHbn() {
		super();
		this.permission = BPPacket.SIGNAL_PERMISSION_CODE_RO;
		this.minVal = BPPacket.VAL_I16_UNLIMIT;
		this.minVal = BPPacket.VAL_I16_UNLIMIT;
		this.defVal = 0;
		this.enStatistics = true;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Short getPermission() {
		return permission;
	}
	@Override
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Short getMinVal() {
		return minVal;
	}
	public void setMinVal(Short minVal) {
		this.minVal = minVal;
	}
	public Short getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Short maxVal) {
		this.maxVal = maxVal;
	}
	public Short getDefVal() {
		return defVal;
	}
	public void setDefVal(Short defVal) {
		this.defVal = defVal;
	}
	@Override
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	@Override
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	public Long getCustomSignalId() {
		return customSignalId;
	}
	@Override
	public void setCustomSignalId(Long customSignalId) {
		this.customSignalId = customSignalId;
	}

	@Override
	public String toString() {
		return "CustomSignalI16InfoHbn [id=" + id + ", permission=" + permission + ", minVal=" + minVal + ", maxVal="
				+ maxVal + ", defVal=" + defVal + ", enStatistics=" + enStatistics + ", customSignalId="
				+ customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_IINT16;
	}
	
	@Override
	public long saveToDb(Session session) {
		Long ret;
		try {
			ret = (Long)session.save(this);
		} catch(Exception e) {
			ret = -1L;
		}
		return ret;
	}
    
	@Override
	public Object getDefaultValue() {
		return getDefVal();
	}
	
	@Override
	public boolean checkSignalValueUnformed(Object v) {
		boolean ret = false;
		try {
			Short value = (Short) v;
			if (maxVal != BPPacket.VAL_U16_UNLIMIT && value > maxVal) {
				/* value too big */
				ret = true;
			} else if (minVal != BPPacket.VAL_U16_UNLIMIT && value < minVal) {
				/* value too small */
				ret = true;
			}
		} catch (Exception e) {
			ret = true;
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}
    
}
