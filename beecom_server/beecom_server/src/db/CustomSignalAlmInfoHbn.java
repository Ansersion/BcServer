package db;

import org.hibernate.Session;

public class CustomSignalAlmInfoHbn extends SignalInterface {
    private Long id;
    private Long cusSigNameLangId;
    private Byte almClass;
    private Short dlyBeforeAlm;
    private Short dlyAfterAlm;
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
	public Byte getAlmClass() {
		return almClass;
	}
	public void setAlmClass(Byte almClass) {
		this.almClass = almClass;
	}
	public Short getDlyBeforeAlm() {
		return dlyBeforeAlm;
	}
	public void setDlyBeforeAlm(Short dlyBeforeAlm) {
		this.dlyBeforeAlm = dlyBeforeAlm;
	}
	public Short getDlyAfterAlm() {
		return dlyAfterAlm;
	}
	public void setDlyAfterAlm(Short dlyAfterAlm) {
		this.dlyAfterAlm = dlyAfterAlm;
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
		return "CustomSignalAlmInfoHbn [id=" + id + ", cusSigNameLangId=" + cusSigNameLangId + ", almClass=" + almClass
				+ ", dlyBeforeAlm=" + dlyBeforeAlm + ", dlyAfterAlm=" + dlyAfterAlm + ", customSignalId="
				+ customSignalId + "]";
	}
	@Override
	public int getValType() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	

	
    
	
    
}
