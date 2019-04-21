package db;

public class BPLanguageId {
	
	private BPLanguageId() {
		throw new IllegalStateException("Utility class");
	}
	  
	public static final int CHINESE = 0x07; 
	public static final int ENGLISH = 0x06; 
	public static final int FRENCH = 0x05; 
	public static final int RUSSIAN = 0x04; 
	public static final int ARABIC = 0x03; 
	public static final int SPANISH = 0x02; 
	
	public static final int STANDARD_ALL_LANG_MASK = 0xFE;

}
