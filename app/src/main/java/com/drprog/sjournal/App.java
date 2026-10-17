package com.drprog.sjournal;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowInsets;

public class App extends Application {

    public static App sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            registerActivityLifecycleCallbacks(new EdgeToEdgeInsetsCallbacks());
        }
    }

    /**
     * Edge-to-edge is enforced since targetSdk 35, so content is drawn behind the action bar,
     * system bars and keyboard. Restore the old layout by padding the content frame.
     */
    private static class EdgeToEdgeInsetsCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityPostCreated(Activity activity, Bundle savedInstanceState) {
            int actionBarHeight = getActionBarHeight(activity);
            View content = activity.findViewById(android.R.id.content);
            content.setOnApplyWindowInsetsListener((v, insets) -> {
                // Root insets: the ones passed here are already partly consumed by the action bar
                android.graphics.Insets bars = v.getRootWindowInsets().getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                                | WindowInsets.Type.ime());
                v.setPadding(bars.left, bars.top + actionBarHeight, bars.right, bars.bottom);
                return WindowInsets.CONSUMED;
            });
        }

        private static int getActionBarHeight(Activity activity) {
            if (activity.getActionBar() == null || !activity.getActionBar().isShowing()) return 0;
            TypedValue value = new TypedValue();
            if (!activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true)) {
                return 0;
            }
            return TypedValue.complexToDimensionPixelSize(value.data,
                    activity.getResources().getDisplayMetrics());
        }

        @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
        @Override public void onActivityStarted(Activity activity) {}
        @Override public void onActivityResumed(Activity activity) {}
        @Override public void onActivityPaused(Activity activity) {}
        @Override public void onActivityStopped(Activity activity) {}
        @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        @Override public void onActivityDestroyed(Activity activity) {}
    }
}
