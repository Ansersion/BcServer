package db;

import java.util.Date;

public class SnInfoHbn {
	private Long id;
    private String sn;
    private Long developUserId;
    private Date activiteDate;
    private Date expiredDate;
    private Integer existTime;
    
	public SnInfoHbn() {
		super();
		activiteDate = new Date();
		expiredDate = new Date();
		existTime = -1;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Long getDevelopUserId() {
		return developUserId;
	}
	public void setDevelopUserId(Long developUserId) {
		this.developUserId = developUserId;
	}
	public Date getActiviteDate() {
		return activiteDate;
	}
	public void setActiviteDate(Date activiteDate) {
		this.activiteDate = activiteDate;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Integer getExistTime() {
		return existTime;
	}
	public void setExistTime(Integer existTime) {
		this.existTime = existTime;
	}
	@Override
	public String toString() {
		return "SnInfoHbn [id=" + id + ", sn=" + sn + ", developUserId=" + developUserId + ", activiteDate="
				+ activiteDate + ", expiredDate=" + expiredDate + ", existTime=" + existTime + "]";
	}

    
    
}
