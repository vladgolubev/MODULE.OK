package ua.samosfator.moduleok.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREF_FILE_NAME = "APP_SETTINGS";
    private static SharedPreferences sharedPreferences;

    public static void init(Context applicationContext) {
        if (sharedPreferences == null) {
            sharedPreferences = applicationContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String read(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}
