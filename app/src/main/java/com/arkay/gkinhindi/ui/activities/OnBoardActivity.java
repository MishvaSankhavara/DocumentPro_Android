package com.arkay.gkinhindi.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.adapter_reader.OnboardingScreenAdapter;

import java.util.ArrayList;

public class OnBoardActivity extends AppCompatActivity {

    public AnimationSet titleAnimationSet;
    public AnimationSet descriptionAnimationSet;
    ViewPager2 onboardingViewPager;
    public final ArrayList<View> listView = new ArrayList<>();
    public LinearLayout nextButtonLayout;
    public AppCompatTextView nextButtonText;
    public View viewGuide1;
    public View viewGuide2;
    public View viewGuide3;
    public View indicator1;
    public View indicator2;
    public View indicator3;
    public int previousPagePosition;
    public AppCompatTextView titlePageOneText;
    public AppCompatTextView descriptionPageOneText;
    public AppCompatTextView titlePageTwoText;
    public AppCompatTextView descriptionPageTwoText;
    public AppCompatTextView titlePageThreeText;
    public AppCompatTextView descriptionPageThreeText;

    private final android.os.Handler autoScrollHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private boolean isFullAd1Failed = false;
    private boolean isFullAd2Failed = false;

    public static final class ViewPagerChange extends ViewPager2.OnPageChangeCallback {
        public final OnBoardActivity activity;

        public ViewPagerChange(OnBoardActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            boolean isMovingForward = position >= activity.previousPagePosition;
            activity.cancelAutoScrollTimer();
            activity.updateIndicatorAnimation(position);
            if (position > activity.previousPagePosition) {
                activity.animatePageText(position);
            }
            activity.previousPagePosition = position;

            if (position == 1) {
                if (activity.isFullAd1Failed || !Constants.enable_all_ads || (!Constants.native_onboarding && !Constants.native_onboarding_2ID)) {
                    if (activity.onboardingViewPager != null) {
                        int target = isMovingForward ? 2 : 0;
                        activity.onboardingViewPager.setCurrentItem(target, false);
                    }
                }
            } else if (position == 3) {
                if (activity.isFullAd2Failed || !Constants.enable_all_ads || (!Constants.native_onboarding && !Constants.native_onboarding_2ID)) {
                    if (activity.onboardingViewPager != null) {
                        int target = isMovingForward ? 4 : 2;
                        activity.onboardingViewPager.setCurrentItem(target, false);
                    }
                }
            }
        }
    }

    public void startAutoScrollTimer(final int targetItem) {
        cancelAutoScrollTimer();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (onboardingViewPager != null) {
                    onboardingViewPager.setCurrentItem(targetItem);
                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 10000);
    }

    public void cancelAutoScrollTimer() {
        if (autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollRunnable = null;
        }
    }

    @Override
    protected void onDestroy() {
        cancelAutoScrollTimer();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.arkay.gkinhindi.utils.AnalyticsHelper.logScreen(this, "Onboarding Screen");
    }

    private void initViewPager() {
        if (onboardingViewPager != null) {
            OnboardingScreenAdapter aVar = new OnboardingScreenAdapter();
            aVar.list_OnboardingScreen = this.listView;
            onboardingViewPager.setAdapter(aVar);
            onboardingViewPager.setCurrentItem(0);
            onboardingViewPager.registerOnPageChangeCallback(new ViewPagerChange(this));
        }
    }

    private void initView() {
        onboardingViewPager = findViewById(R.id.guide_vp);
        this.nextButtonLayout = findViewById(R.id.next_layout);
        this.nextButtonText = findViewById(R.id.next_start_tv);
        LayoutInflater from = LayoutInflater.from(this);

        this.viewGuide1 = from.inflate(R.layout.layout_on_boarding1, null);
        this.viewGuide2 = from.inflate(R.layout.layout_on_boarding2, null);
        this.viewGuide3 = from.inflate(R.layout.layout_on_boarding3, null);

        View viewFullAd1 = from.inflate(R.layout.layout_on_boarding_full_ad, null);
        View viewFullAd2 = from.inflate(R.layout.layout_on_boarding_full_ad, null);

        this.indicator1 = findViewById(R.id.v_indicator1);
        this.indicator2 = findViewById(R.id.v_indicator2);
        this.indicator3 = findViewById(R.id.v_indicator3);

        // Index 0: Page 1
        if (this.viewGuide1 != null) {
            this.listView.add(this.viewGuide1);
            this.titlePageOneText = this.viewGuide1.findViewById(R.id.title_tv);
            this.descriptionPageOneText = this.viewGuide1.findViewById(R.id.subtitle_tv);
        }

        // Index 1: Full Screen Native Ad 1 (after Page 1)
        if (viewFullAd1 != null) {
            this.listView.add(viewFullAd1);
            android.widget.FrameLayout fullAdFrame1 = viewFullAd1.findViewById(R.id.full_native_ad_frame);
            View shimmer1 = viewFullAd1.findViewById(R.id.shimmer_full_ad);
            com.arkay.gkinhindi.utils.AdsUtils.showFullScreenNativeAd(
                    this,
                    fullAdFrame1,
                    shimmer1,
                    BuildConfig.native_onboarding,
                    BuildConfig.native_onboarding_2ID,
                    Constants.native_onboarding,
                    Constants.native_onboarding_2ID,
                    v -> {
                        if (onboardingViewPager != null) {
                            onboardingViewPager.setCurrentItem(2);
                        }
                    },
                    new com.arkay.gkinhindi.utils.AdsUtils.OnNativeAdStateListener() {
                        @Override
                        public void onAdLoaded() {
                            isFullAd1Failed = false;
                        }

                        @Override
                        public void onAdFailed() {
                            isFullAd1Failed = true;
                            if (onboardingViewPager != null && onboardingViewPager.getCurrentItem() == 1) {
                                onboardingViewPager.setCurrentItem(2, false);
                            }
                        }
                    }
            );
        }

        // Index 2: Page 2
        if (this.viewGuide2 != null) {
            this.listView.add(this.viewGuide2);
            this.titlePageTwoText = this.viewGuide2.findViewById(R.id.title_tv2);
            this.descriptionPageTwoText = this.viewGuide2.findViewById(R.id.subtitle_tv2);
        }

        // Index 3: Full Screen Native Ad 2 (after Page 2)
        if (viewFullAd2 != null) {
            this.listView.add(viewFullAd2);
            android.widget.FrameLayout fullAdFrame2 = viewFullAd2.findViewById(R.id.full_native_ad_frame);
            View shimmer2 = viewFullAd2.findViewById(R.id.shimmer_full_ad);
            com.arkay.gkinhindi.utils.AdsUtils.showFullScreenNativeAd(
                    this,
                    fullAdFrame2,
                    shimmer2,
                    BuildConfig.native_onboarding,
                    BuildConfig.native_onboarding_2ID,
                    Constants.native_onboarding,
                    Constants.native_onboarding_2ID,
                    v -> {
                        if (onboardingViewPager != null) {
                            onboardingViewPager.setCurrentItem(4);
                        }
                    },
                    new com.arkay.gkinhindi.utils.AdsUtils.OnNativeAdStateListener() {
                        @Override
                        public void onAdLoaded() {
                            isFullAd2Failed = false;
                        }

                        @Override
                        public void onAdFailed() {
                            isFullAd2Failed = true;
                            if (onboardingViewPager != null && onboardingViewPager.getCurrentItem() == 3) {
                                onboardingViewPager.setCurrentItem(4, false);
                            }
                        }
                    }
            );
        }

        // Index 4: Page 3
        if (this.viewGuide3 != null) {
            this.listView.add(this.viewGuide3);
            this.titlePageThreeText = this.viewGuide3.findViewById(R.id.title_tv3);
            this.descriptionPageThreeText = this.viewGuide3.findViewById(R.id.subtitle_tv3);
        }

        nextButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPagePosition == 0) {
                    boolean ad1Disabled = isFullAd1Failed || !Constants.enable_all_ads || (!Constants.native_onboarding && !Constants.native_onboarding_2ID);
                    onboardingViewPager.setCurrentItem(ad1Disabled ? 2 : 1, true);
                } else if (previousPagePosition == 1) {
                    onboardingViewPager.setCurrentItem(2, true);
                } else if (previousPagePosition == 2) {
                    boolean ad2Disabled = isFullAd2Failed || !Constants.enable_all_ads || (!Constants.native_onboarding && !Constants.native_onboarding_2ID);
                    onboardingViewPager.setCurrentItem(ad2Disabled ? 4 : 3, true);
                } else if (previousPagePosition == 3) {
                    onboardingViewPager.setCurrentItem(4, true);
                } else {
                    finishOnboarding();
                }
            }
        });

        android.widget.FrameLayout adContainer = findViewById(R.id.native_ad_container);
        if (adContainer != null) {
            com.arkay.gkinhindi.utils.AdsUtils.showLargeNativeAd(
                    this,
                    adContainer,
                    BuildConfig.native_onboarding,
                    BuildConfig.native_onboarding_2ID,
                    Constants.native_onboarding, //Constants.native_onboarding
                    Constants.native_onboarding_2ID //Constants.native_onboarding_2ID
            );
        }
    }

    private void finishOnboarding() {
        PreferenceUtils.getInstance(OnBoardActivity.this)
                .setBoolean(Constants.PREF_GUIDE_COMPLETED, true);
        startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
        finish();
    }

    public final void animatePageText(int position) {
        if (position == 0) {
            animateTitleText(this.titlePageOneText);
            animateDescriptionText(this.descriptionPageOneText);
        } else if (position == 2) {
            animateTitleText(this.titlePageTwoText);
            animateDescriptionText(this.descriptionPageTwoText);
        } else if (position == 4) {
            animateTitleText(this.titlePageThreeText);
            animateDescriptionText(this.descriptionPageThreeText);
        }
    }

    public final void updateIndicatorAnimation(int position) {
        View bottomContainer = findViewById(R.id.bottom_container);
        View adContainer = findViewById(R.id.native_ad_container);

        if (position == 1 || position == 3) {
            if (bottomContainer != null) bottomContainer.setVisibility(View.GONE);
            if (adContainer != null) adContainer.setVisibility(View.GONE);
            return;
        }

        if (bottomContainer != null) bottomContainer.setVisibility(View.VISIBLE);

        if (position == 2) {
            if (adContainer != null) adContainer.setVisibility(View.GONE);
        } else if (adContainer != null && Constants.enable_all_ads && (Constants.native_onboarding || Constants.native_onboarding_2ID)) {
            adContainer.setVisibility(View.VISIBLE);
        }

        if (position == 0) {
            updateIndicator(this.indicator1, true);
            updateIndicator(this.indicator2, false);
            updateIndicator(this.indicator3, false);
            if (nextButtonText != null) nextButtonText.setText(R.string.action_next);
        } else if (position == 2) {
            updateIndicator(this.indicator1, false);
            updateIndicator(this.indicator2, true);
            updateIndicator(this.indicator3, false);
            if (nextButtonText != null) nextButtonText.setText(R.string.action_next);
        } else if (position == 4) {
            updateIndicator(this.indicator1, false);
            updateIndicator(this.indicator2, false);
            updateIndicator(this.indicator3, true);
            if (nextButtonText != null) nextButtonText.setText(R.string.action_get_start);
        }
    }

    public final void animateDescriptionText(AppCompatTextView tvDes) {
        if (this.descriptionAnimationSet == null) {
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.dp_75);
            // if (this.H) {
            // dimensionPixelSize = -dimensionPixelSize;
            // }
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            TranslateAnimation translateAnimation = new TranslateAnimation(dimensionPixelSize, 0.0f, 0.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(true);
            this.descriptionAnimationSet = animationSet;
            animationSet.setDuration(350);
            AnimationSet animationSet2 = this.descriptionAnimationSet;
            if (animationSet2 != null) {
                animationSet2.addAnimation(alphaAnimation);
            }
            AnimationSet animationSet3 = this.descriptionAnimationSet;
            if (animationSet3 != null) {
                animationSet3.addAnimation(translateAnimation);
            }
            AnimationSet animationSet4 = this.descriptionAnimationSet;
            if (animationSet4 != null) {
                animationSet4.setStartOffset(50);
            }
        }
        if (tvDes != null) {
            tvDes.startAnimation(this.descriptionAnimationSet);
        }
    }

    public final void updateIndicator(View view, boolean selected) {
        int idResource;
        int width;
        if (view != null) {
            Resources resources = getResources();
            if (selected) {
                idResource = R.drawable.shape_blue_indicator;
                width = (int) (32 * resources.getDisplayMetrics().density);
            } else {
                idResource = R.drawable.shape_gray_indicator;
                width = (int) (12 * resources.getDisplayMetrics().density);
            }
            view.setBackground(ResourcesCompat.getDrawable(resources, idResource, null));
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = width;
            }
            view.setLayoutParams(layoutParams);
        }
    }

    public final void animateTitleText(AppCompatTextView tvTitle) {
        if (titleAnimationSet == null) {
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.dp_60);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            TranslateAnimation translateAnimation = new TranslateAnimation(dimensionPixelSize, 0.0f, 0.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(true);
            titleAnimationSet = animationSet;
            animationSet.setDuration(350);
            AnimationSet animationSet2 = this.titleAnimationSet;
            if (animationSet2 != null) {
                animationSet2.addAnimation(alphaAnimation);
            }
            AnimationSet animationSet3 = this.titleAnimationSet;
            if (animationSet3 != null) {
                animationSet3.addAnimation(translateAnimation);
            }
        }
        if (tvTitle != null) {
            tvTitle.startAnimation(this.titleAnimationSet);
        }
    }
}