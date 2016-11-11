package net.jankenpoi.sudokuki.preferences;

import java.util.HashMap;

public class UserPreferences {

	private static UserPreferences INSTANCE = new UserPreferences();
	
	static {
		INSTANCE.set("minRating", Integer.valueOf(0));
		INSTANCE.set("maxRating", Integer.valueOf(5700));
		INSTANCE.set("numbersMode", Integer.valueOf(0));
	}

	private final HashMap<String, Object> map = new HashMap<String, Object>();
	
	private UserPreferences() {
	}
	
	public static UserPreferences getInstance() {
		return INSTANCE;
	}
	
	public final Integer getInteger(String name, Integer defaultInteger) {
		Object obj = map.get(name);
		if (!(obj instanceof Integer)) {
			return defaultInteger;
		}
		Integer anInteger = (Integer)obj;
		return anInteger;
	}
	
	public final Boolean getBoolean(String name, Boolean defaultBoolean) {
		Object obj = map.get(name);
		if (!(obj instanceof Boolean)) {
			return defaultBoolean;
		}
		Boolean aBoolean = (Boolean)obj;
		return aBoolean;
	}
	
	public final boolean set(String name, Object pref) {
		if (name == null  || pref == null) {
			return false;
		}
		Object oldPref = map.put(name, pref);
		if (oldPref == null) {
			return true;
		}
		else {
			return false;
		}
	}

}
