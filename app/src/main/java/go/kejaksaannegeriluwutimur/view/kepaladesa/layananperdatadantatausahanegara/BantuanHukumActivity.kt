package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import go.kejaksaannegeriluwutimur.R

class BantuanHukumActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bantuan_hukum)

        imgBack.setOnClickListener { finish() }
    }
}