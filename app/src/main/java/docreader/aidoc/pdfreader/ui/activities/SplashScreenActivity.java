package docreader.aidoc.pdfreader.ui.activities;

import com.docpro.scanner.settings.LocaleSelectionActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.PreferenceUtils;
import docreader.aidoc.pdfreader.utils.Utils;

import android.widget.ProgressBar;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 2000;
    private long remainingSeconds;
    private ProgressBar pbSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.act_splash);
        pbSplash = findViewById(R.id.pb_splash);
        startSplashTimer();
    }

    private void startSplashTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(SPLASH_DURATION_MS, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
                if (pbSplash != null) {
                    int progress = (int) (((SPLASH_DURATION_MS - millisUntilFinished) / (float) SPLASH_DURATION_MS) * 100);
                    pbSplash.setProgress(progress);
                }
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;
                handleNavigationAfterSplash();
            }
        };
        countDownTimer.start();
    }

    private void handleNavigationAfterSplash() {
        if (getIntent() != null && getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            if (uri != null) {
                intent.putExtra(AppGlobalConstants.EXTRA_DATA_FROM_OUTSIDE, uri.toString());
            } else {
                Log.e("Thang123", "URI is null");
            }
            startActivity(intent);
            finish();
        } else {
            if (!PreferenceUtils.getInstance(SplashScreenActivity.this).getBoolean(AppGlobalConstants.PREF_LANGUAGE_SET,
                    false)) {
                startActivity(new Intent(SplashScreenActivity.this, LocaleSelectionActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}