package com.example.documenpro.ui.activities;

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

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.adapter_reader.OnboardingScreenAdapter;
import com.example.documenpro.advertisement.OnAdDismissedListener;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.advertisement.AdMobNativeAdManager;

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

    public static final class ViewPagerChange extends ViewPager2.OnPageChangeCallback {
        public final OnBoardActivity activity;

        public ViewPagerChange(OnBoardActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            activity.updateIndicatorAnimation(position);
            if (position > activity.previousPagePosition) {
                activity.animatePageText(position);
            }
            activity.previousPagePosition = position;
            if (position == 2) {
                AppCompatTextView appCompatTextView = activity.nextButtonText;
                if (appCompatTextView != null) {
                    appCompatTextView.setText(R.string.str_action_start);
                }
            } else {
                AppCompatTextView appCompatTextView2 = activity.nextButtonText;
                if (appCompatTextView2 != null) {
                    appCompatTextView2.setText(R.string.str_next);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_on_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AdMobNativeAdManager.showNativeBanner1_AdMob(this, null);
        initView();
        initViewPager();
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
        this.indicator1 = findViewById(R.id.v_indicator1);
        this.indicator2 = findViewById(R.id.v_indicator2);
        this.indicator3 = findViewById(R.id.v_indicator3);
        View view = this.viewGuide1;
        if (view != null) {
            this.listView.add(view);
            this.titlePageOneText = view.findViewById(R.id.title_tv);
            this.descriptionPageOneText = view.findViewById(R.id.subtitle_tv);
        }
        View view3 = this.viewGuide2;
        if (view3 != null) {
            listView.add(view3);
            this.titlePageTwoText = view3.findViewById(R.id.title_tv2);
            this.descriptionPageTwoText = view3.findViewById(R.id.subtitle_tv2);
        }
        View view4 = this.viewGuide3;
        if (view4 != null) {
            listView.add(view4);
            this.titlePageThreeText = view4.findViewById(R.id.title_tv3);
            this.descriptionPageThreeText = view4.findViewById(R.id.subtitle_tv3);
        }
        nextButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previousPagePosition == 0) {
                    onboardingViewPager.setCurrentItem(1);
                } else if (previousPagePosition == 1) {
                    onboardingViewPager.setCurrentItem(2);
                } else {
                    PreferenceUtils.getInstance(OnBoardActivity.this).setBoolean(AppGlobalConstants.PREF_GUIDE_COMPLETED, true);


                    AdManager.showAds_AdManager(OnBoardActivity.this, new OnAdDismissedListener() {
                        @Override
                        public void OnAdDismissedListener() {
                            startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }

    public final void animatePageText(int position) {
        if (position == 0) {
            animateTitleText(this.titlePageOneText);
            animateDescriptionText(this.descriptionPageOneText);
        } else if (position == 1) {
            animateTitleText(this.titlePageTwoText);
            animateDescriptionText(this.descriptionPageTwoText);
        } else if (position == 2) {
            animateTitleText(this.titlePageThreeText);
            animateDescriptionText(this.descriptionPageThreeText);
        }
    }

    public final void updateIndicatorAnimation(int position) {
        if (position == 0) {
            updateIndicator(this.indicator1, true);
            updateIndicator(this.indicator2, false);
            updateIndicator(this.indicator3, false);
        } else if (position == 1) {
            updateIndicator(this.indicator1, false);
            updateIndicator(this.indicator2, true);
            updateIndicator(this.indicator3, false);
        } else if (position == 2) {
            updateIndicator(this.indicator1, false);
            updateIndicator(this.indicator2, false);
            updateIndicator(this.indicator3, true);
        }
    }

    public final void animateDescriptionText(AppCompatTextView tvDes) {
        if (this.descriptionAnimationSet == null) {
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.dp_75);
//            if (this.H) {
//                dimensionPixelSize = -dimensionPixelSize;
//            }
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
        int idDimen;
        if (view != null) {
            Resources resources = getResources();
            if (selected) {
                idResource = R.drawable.shape_red_radius_46;
            } else {
                idResource = R.drawable.shape_gray_radius_46;
            }
            view.setBackground(ResourcesCompat.getDrawable(resources, idResource, null));
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                Resources resources2 = getResources();
                if (selected) {
                    idDimen = R.dimen.dp_44;
                } else {
                    idDimen = R.dimen.dp_16;
                }
                layoutParams.width = resources2.getDimensionPixelSize(idDimen);
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