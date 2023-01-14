package go.kejaksaannegeriluwutimur.view.kepaladesa

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants.MSG_TERJADI_KESALAHAN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_IS_LOGIN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_ROLE
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.view.kepaladesa.fragment.ChatFragment
import go.kejaksaannegeriluwutimur.view.kepaladesa.fragment.HomeFragment
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tab_layout_menu_home) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpUi()
    }

    override fun onResume() {
        super.onResume()

        isLogin()
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(PREF_USER_ROLE, null)

        if (!isLogin && userRole != ROLE_KEPALA_DESA) {
            Toast.makeText(applicationContext, MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpUi() {
        loadFragment(HomeFragment())

        tabLayout.getTabAt(1)?.apply {
            orCreateBadge
            badge?.maxCharacterCount = 3
            badge?.number = 999
            badge?.isVisible = false
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Dipanggil ketika tab memasuki state/keadaan yang dipilih.

                if (tab.text.toString() == getString(R.string.home)) {
                    loadFragment(HomeFragment())
                } else if (tab.text.toString() == getString(R.string.chat)) {
                    loadFragment(ChatFragment())
                } else {
                    //
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //Dipanggil saat tab keluar dari keadaan yang dipilih.
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                //Dipanggil ketika tab yang sudah dipilih, dipilih lagi oleh user.
            }
        })

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view_home, fragment)
            commit()
        }
    }

}