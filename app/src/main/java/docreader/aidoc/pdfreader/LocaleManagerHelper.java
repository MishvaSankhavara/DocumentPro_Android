package docreader.aidoc.pdfreader;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class LocaleManagerHelper {
    public static Context applyLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLayoutDirection(locale);
        configuration.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }

    public static Context attachLocale(Context context) {
        return applyLocale(context, PreferenceUtils.getInstance(context).getString(AppGlobalConstants.PREF_LANGUAGE_KEY, "en"));
    }
}
