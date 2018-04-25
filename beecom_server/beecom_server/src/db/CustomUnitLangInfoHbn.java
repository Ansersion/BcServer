package db;

public class CustomUnitLangInfoHbn {
	private Long id;
    private String unitLang;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUnitLang() {
		return unitLang;
	}
	public void setUnitLang(String unitLang) {
		this.unitLang = unitLang;
	}
	@Override
	public String toString() {
		return "CustomUnitLangInfoHbn [id=" + id + ", unitLang=" + unitLang + "]";
	}
    
    
}
