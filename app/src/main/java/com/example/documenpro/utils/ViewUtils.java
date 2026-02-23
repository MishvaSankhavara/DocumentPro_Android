package com.example.documenpro.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.example.documenpro.R;


public class ViewUtils {
    public static void showViewTop(View view1, @Nullable View view2) {
        Animation anim = new TranslateAnimation(0, 0, -view1.getHeight(), 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view1.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (view2 != null)
                    view2.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view1.startAnimation(anim);
    }
    public static void showViewBottom(View view) {
        Animation anim = new TranslateAnimation(0, 0, view.getHeight(), 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }
    public static void hideViewBottom(View view) {
        Animation anim = new TranslateAnimation(0, 0, 0, view.getHeight());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    public static boolean checkViewVisibility(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }
    public static ObjectAnimator startAnim(View view) {
        int dimensionPixelOffset = view.getResources().getDimensionPixelOffset(R.dimen.dp_5);
        float f = (float) (-dimensionPixelOffset);
        float f2 = (float) dimensionPixelOffset;
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.1f, f), Keyframe.ofFloat(0.26f, f2), Keyframe.ofFloat(0.42f, f), Keyframe.ofFloat(0.58f, f2), Keyframe.ofFloat(0.74f, f), Keyframe.ofFloat(0.9f, f2), Keyframe.ofFloat(1.0f, 0.0f))).setDuration(500);
    }
    public static void hideViewTop(View view1, @Nullable View view2) {
        Animation animation = new TranslateAnimation(0, 0, 0, -view1.getHeight());
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view1.setVisibility(View.INVISIBLE);
                if (view2 != null) {
                    view2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view1.startAnimation(animation);
    }

    public static void showKeyboard(Context var0) {
        ((InputMethodManager) var0.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


}
