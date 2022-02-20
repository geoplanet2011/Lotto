package ge.gogichaishvili.lotto.app.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowInsets

object Utils {

    //hide bottom navigation buttons and clock
    fun hideSystemUI(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.setDecorFitsSystemWindows(false)
            if (activity.window.insetsController != null) {
                activity.window.insetsController!!
                    .hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                activity.window.insetsController!!.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        } else {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

}