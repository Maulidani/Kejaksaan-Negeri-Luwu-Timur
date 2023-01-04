package go.kejaksaannegeriluwutimur.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.Constants.Companion.changeStatusBarColor
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        changeStatusBarColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            ), false
        )
        
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

}