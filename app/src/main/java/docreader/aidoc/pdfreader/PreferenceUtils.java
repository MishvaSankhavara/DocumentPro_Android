package docreader.aidoc.pdfreader;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static PreferenceUtils instance;

    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    private PreferenceUtils(Context context) {
        String PREF_KEY = "SharedPreferenceUtils";
        this.sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceUtils(context);
        }
        return instance;
    }

    public String getString(String str, String str2) {
        return sharedPreferences.getString(str, str2);
    }

    public void setBoolean(String str, boolean z) {
        this.editor.putBoolean(str, z);
        this.editor.commit();
    }

    public void setString(String str, String str2) {
        editor.putString(str, str2);
        editor.commit();
    }

    public void setInt(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.commit();
    }

    public boolean getBoolean(String str, boolean z) {
        return this.sharedPreferences.getBoolean(str, z);
    }

    public int getInt(String str, int i) {
        return this.sharedPreferences.getInt(str, i);
    }
}
