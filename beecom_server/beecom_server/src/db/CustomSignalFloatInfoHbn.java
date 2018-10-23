package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalFloatInfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Short accuracy;
    private Float minVal;
    private Float maxVal;
    private Float defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
	public CustomSignalFloatInfoHbn() {
		super();
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
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Short getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Short accuracy) {
		this.accuracy = accuracy;
	}
	public Float getMinVal() {
		return minVal;
	}
	public void setMinVal(Float minVal) {
		this.minVal = minVal;
	}
	public Float getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Float maxVal) {
		this.maxVal = maxVal;
	}
	public Float getDefVal() {
		return defVal;
	}
	public void setDefVal(Float defVal) {
		this.defVal = defVal;
	}
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	public Long getCustomSignalId() {
		return customSignalId;
	}
	public void setCustomSignalId(Long customSignalId) {
		this.customSignalId = customSignalId;
	}

	@Override
	public String toString() {
		return "CustomSignalFloatInfoHbn [id=" + id + ", permission=" + permission + ", accuracy=" + accuracy
				+ ", minVal=" + minVal + ", maxVal=" + maxVal + ", defVal=" + defVal + ", enStatistics=" + enStatistics
				+ ", customSignalId=" + customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_FLOAT;
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
}
