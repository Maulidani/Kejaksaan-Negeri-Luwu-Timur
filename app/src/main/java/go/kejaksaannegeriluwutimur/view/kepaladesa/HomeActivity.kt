package go.kejaksaannegeriluwutimur.view.kepaladesa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants.Companion.changeStatusBarColor
import go.kejaksaannegeriluwutimur.view.kepaladesa.fragment.ChatFragment
import go.kejaksaannegeriluwutimur.view.kepaladesa.fragment.HomeFragment
import go.kejaksaannegeriluwutimur.view.login.fragment.AdminFragment
import go.kejaksaannegeriluwutimur.view.login.fragment.KepalaDesaFragment

class HomeActivity : AppCompatActivity() {
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tab_layout_menu_home) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loadFragment(HomeFragment())
        setOnCLick()
    }

    private fun setOnCLick() {

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