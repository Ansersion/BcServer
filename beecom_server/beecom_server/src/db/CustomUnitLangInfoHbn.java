package db;

public class CustomUnitLangInfoHbn {
	private Long id;
    private Long unitLang;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUnitLang() {
		return unitLang;
	}
	public void setUnitLang(Long unitLang) {
		this.unitLang = unitLang;
	}
	@Override
	public String toString() {
		return "CustomUnitLangInfoHbn [id=" + id + ", unitLang=" + unitLang + "]";
	}
    
    
}
