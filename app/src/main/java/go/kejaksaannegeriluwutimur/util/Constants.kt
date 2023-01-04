package go.kejaksaannegeriluwutimur.util

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat

class Constants {
    companion object {


        fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color

            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                isLight
        }
    }
}