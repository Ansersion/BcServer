package db;

public class CustomGroupLangInfoHbn {
	private Long id;
    private String groupLang;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroupLang() {
		return groupLang;
	}
	public void setGroupLang(String groupLang) {
		this.groupLang = groupLang;
	}
	@Override
	public String toString() {
		return "CustomGroupLangInfoHbn [id=" + id + ", groupLang=" + groupLang + "]";
	}
    
    

}
