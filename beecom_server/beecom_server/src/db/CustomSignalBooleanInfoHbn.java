package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalBooleanInfoHbn extends SignalInterface {
    private Long id;
    private Long cusSigNameLangId;
    private Short permission;
    private Boolean defVal;
    private Integer groupLangId;
    private Long cusGroupLangId;
    private Boolean enStatistics;
    private Long customSignalId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCusSigNameLangId() {
		return cusSigNameLangId;
	}
	public void setCusSigNameLangId(Long cusSigNameLangId) {
		this.cusSigNameLangId = cusSigNameLangId;
	}
	public Short getPermission() {
		return permission;
	}
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Boolean getDefVal() {
		return defVal;
	}
	public void setDefVal(Boolean defVal) {
		this.defVal = defVal;
	}
	public Integer getGroupLangId() {
		return groupLangId;
	}
	public void setGroupLangId(Integer groupLangId) {
		this.groupLangId = groupLangId;
	}
	public Long getCusGroupLangId() {
		return cusGroupLangId;
	}
	public void setCusGroupLangId(Long cusGroupLangId) {
		this.cusGroupLangId = cusGroupLangId;
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
		return "CustomSignalBooleanInfoHbn [id=" + id + ", cusSigNameLangId=" + cusSigNameLangId + ", permission="
				+ permission + ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", cusGroupLangId="
				+ cusGroupLangId + ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_BOOLEAN;
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
    
}
