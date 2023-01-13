package go.kejaksaannegeriluwutimur.view.admin

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.LogoutPopUp

class AdminHomeActivity : AppCompatActivity() {
    private val imgLogout: ImageView by lazy { findViewById(R.id.iv_logout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setUpUi()
    }

    private fun setUpUi() {
        val logoutPopUp = LogoutPopUp()

        imgLogout.setOnClickListener {
            logoutPopUp.show(supportFragmentManager, "Logout pop-up")
        }
    }
}