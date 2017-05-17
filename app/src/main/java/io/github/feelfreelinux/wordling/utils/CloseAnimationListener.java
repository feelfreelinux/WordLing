package io.github.feelfreelinux.wordling.utils;

import android.view.View;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;

public class CloseAnimationListener implements Animation.AnimationListener {
    public interface FabCloseListener {
        void onClose();
    }
    WeakReference<View> view, hint;
    FabCloseListener listener;
    public void setView(View view, View tooltip, FabCloseListener listener) {
        this.listener = listener;
        this.view = new WeakReference<View>(view);
        this.hint = new WeakReference<View>(tooltip);

    }
    public void onAnimationEnd(Animation animation) {
        view.get().setVisibility(View.GONE);
        hint.get().setVisibility(View.GONE);
        if (listener != null) listener.onClose();
    }
    public void onAnimationRepeat(Animation animation) {
    }
    public void onAnimationStart(Animation animation) {
    }
}