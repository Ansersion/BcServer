package db;

public class CustomGroupLangInfoHbn {
	private Long id;
    private Long groupLang;
    
    public CustomGroupLangInfoHbn() {
    	
    }
    
	public CustomGroupLangInfoHbn(Long groupLang) {
		super();
		this.groupLang = groupLang;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGroupLang() {
		return groupLang;
	}
	public void setGroupLang(Long groupLang) {
		this.groupLang = groupLang;
	}
	@Override
	public String toString() {
		return "CustomGroupLangInfoHbn [id=" + id + ", groupLang=" + groupLang + "]";
	}
    
    

}
