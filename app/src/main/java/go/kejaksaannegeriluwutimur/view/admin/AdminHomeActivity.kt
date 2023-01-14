package go.kejaksaannegeriluwutimur.view.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_ADMIN
import go.kejaksaannegeriluwutimur.util.LogoutPopUp
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class AdminHomeActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val imgLogout: ImageView by lazy { findViewById(R.id.iv_logout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setUpUi()
    }

    override fun onResume() {
        super.onResume()

        isLogin()
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (!isLogin && userRole != ROLE_ADMIN) {
            Toast.makeText(applicationContext, Constants.MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpUi() {
        val logoutPopUp = LogoutPopUp()

        imgLogout.setOnClickListener {
            logoutPopUp.show(supportFragmentManager, "Logout pop-up")
        }
    }
}