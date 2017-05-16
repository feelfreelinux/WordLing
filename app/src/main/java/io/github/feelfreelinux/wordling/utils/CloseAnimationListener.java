package io.github.feelfreelinux.wordling.utils;

import android.view.View;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;

public class CloseAnimationListener implements Animation.AnimationListener {
    WeakReference<View> view, hint;
    public void setView(View view, View tooltip) {
        this.view = new WeakReference<View>(view);
        this.hint = new WeakReference<View>(tooltip);

    }
    public void onAnimationEnd(Animation animation) {
        view.get().setVisibility(View.GONE);
        hint.get().setVisibility(View.GONE);
    }
    public void onAnimationRepeat(Animation animation) {
    }
    public void onAnimationStart(Animation animation) {
    }
}