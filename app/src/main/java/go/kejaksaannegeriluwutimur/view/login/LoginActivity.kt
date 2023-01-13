package go.kejaksaannegeriluwutimur.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_ADMIN
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.util.Ui.changeStatusBarColor
import go.kejaksaannegeriluwutimur.view.admin.AdminHomeActivity
import go.kejaksaannegeriluwutimur.view.kepaladesa.HomeActivity
import go.kejaksaannegeriluwutimur.view.login.fragment.AdminFragment
import go.kejaksaannegeriluwutimur.view.login.fragment.KepalaDesaFragment
import go.kejaksaannegeriluwutimur.viewmodel.login.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tab_layout_login) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUpUi()
        setUpObservers()
    }

    private fun setUpUi() {
        changeStatusBarColor(
            ContextCompat.getColor(
                applicationContext,
                androidx.constraintlayout.widget.R.color.material_grey_900
            ), false
        )
        loadFragment(KepalaDesaFragment())

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Dipanggil ketika tab memasuki state/keadaan yang dipilih.

                if (tab.text.toString() == getString(R.string.kepala_desa)) {
                    loadFragment(KepalaDesaFragment())
                } else if (tab.text.toString() == getString(R.string.admin)) {
                    loadFragment(AdminFragment())
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

    private fun setUpObservers() {
        loginViewModel.roleIsLogin.observe(this) { role ->
            when (role) {
                ROLE_KEPALA_DESA -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                }
                ROLE_ADMIN -> {
                    startActivity(Intent(applicationContext, AdminHomeActivity::class.java))
                    finish()
                }
                else -> {
                    Toast.makeText(applicationContext, "Terjadi kesalahan", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view_login, fragment)
            commit()
        }
    }
}