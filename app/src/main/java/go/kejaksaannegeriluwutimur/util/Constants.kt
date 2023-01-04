package go.kejaksaannegeriluwutimur.util

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import go.kejaksaannegeriluwutimur.R

class Constants {
    companion object {

        fun MaterialButton.setShowProgress(showProgress: Boolean?, title: String) {

            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            isCheckable = showProgress == false
            text = if (showProgress == true) "" else title

            icon = if (showProgress == true) {
                CircularProgressDrawable(context!!).apply {
                    setStyle(CircularProgressDrawable.DEFAULT)
                    setColorSchemeColors(ContextCompat.getColor(context!!, R.color.white))
                    start()
                }
            } else null

            if (icon != null) { // callback to redraw button icon
                icon.callback = object : Drawable.Callback {
                    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                    }

                    override fun invalidateDrawable(who: Drawable) {
                        this@setShowProgress.invalidate()
                    }

                    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                    }
                }
            }
        }


        fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color

            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                isLight
        }
    }
}