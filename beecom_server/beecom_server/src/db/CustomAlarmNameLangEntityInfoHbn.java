package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomAlarmNameLangEntityInfoHbn implements SignalLanguageInterface {
	private static final Logger logger = LoggerFactory.getLogger(CustomAlarmNameLangEntityInfoHbn.class); 
	private Long id;
	private String chinese;
	private String english;
	private String french;
	private String russian;
	private String arabic;
	private String spanish;
	
	public CustomAlarmNameLangEntityInfoHbn() {
		super();
	}
	
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

	@Override
	public void setLang(int key, String lang) {
		switch(key) {
		case BPLanguageId.CHINESE:
			setChinese(lang);
			break;
		case BPLanguageId.ENGLISH:
			setEnglish(lang);
			break;
		case BPLanguageId.FRENCH:
			setFrench(lang);
			break;
		case BPLanguageId.RUSSIAN:
			setRussian(lang);
			break;
		case BPLanguageId.ARABIC:
			setArabic(lang);
			break;
		case BPLanguageId.SPANISH:
			setSpanish(lang);
			break;
		default:
			logger.error("invalid signal language type {}", key);
			break;
		}
		
	}
}
