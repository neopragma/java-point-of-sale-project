package pos.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

	private static final String BASE_NAME = "messages";
	private ResourceBundle bundle = null;
	private Locale locale = Locale.getDefault();
	
	public String message(String key) {
		return bundle().getString(key);
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	private ResourceBundle bundle() {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		}
		return bundle;
	}
}
