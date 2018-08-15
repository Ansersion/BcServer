/**
 * 
 */
package db;

/**
 * @author Ansersion
 *
 */
public class CustomSignalEnumLangEntityInfoHbn implements SignalLanguageInterface {

	private Long id;
	private String chinese;
	private String english;
	private String french;
	private String russian;
	private String arabic;
	private String spanish;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getChinese() {
		return chinese;
	}
	public void setChinese(String chinese) {
		this.chinese = chinese;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getFrench() {
		return french;
	}
	public void setFrench(String french) {
		this.french = french;
	}
	public String getRussian() {
		return russian;
	}
	public void setRussian(String russian) {
		this.russian = russian;
	}
	public String getArabic() {
		return arabic;
	}
	public void setArabic(String arabic) {
		this.arabic = arabic;
	}
	public String getSpanish() {
		return spanish;
	}
	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}


}
