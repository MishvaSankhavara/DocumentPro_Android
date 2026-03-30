package docreader.aidoc.pdfreader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityBase extends AppCompatActivity {
    public ActivityBase context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        LocaleManagerHelper.applyLocale(context, PreferenceUtils.getInstance(this).getString(AppGlobalConstants.PREF_LANGUAGE_KEY, "en"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManagerHelper.attachLocale(newBase));
    }
}
