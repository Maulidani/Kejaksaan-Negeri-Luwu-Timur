package go.kejaksaannegeriluwutimur.view.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants.Companion.changeStatusBarColor
import go.kejaksaannegeriluwutimur.view.login.fragment.AdminFragment
import go.kejaksaannegeriluwutimur.view.login.fragment.KepalaDesaFragment

class LoginActivity : AppCompatActivity() {
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tab_layout_login) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeStatusBarColor(
            ContextCompat.getColor(
                applicationContext,
                androidx.constraintlayout.widget.R.color.material_grey_900
            ), false
        )
        loadFragment(KepalaDesaFragment())
        setOnCLick()
    }

    private fun setOnCLick() {

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

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view_login, fragment)
            commit()
        }
    }
}