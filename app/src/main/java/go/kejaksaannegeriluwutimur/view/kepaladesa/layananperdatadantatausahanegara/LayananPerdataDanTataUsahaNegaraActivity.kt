package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.ChatPopUp

@AndroidEntryPoint
class LayananPerdataDanTataUsahaNegaraActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val cardLayananPermohonanMoU: CardView by lazy { findViewById(R.id.cv_item_layanan_permohonan_mou) }
    private val cardPelayananBantuanHukum: CardView by lazy { findViewById(R.id.cv_item_pelayanan_bantuan_hukum) }
    private val cardPelayananPertimbanganHukum: CardView by lazy { findViewById(R.id.cv_item_pelayanan_pertimbangan_hukum) }
    private val cardPelayananHukumGratis: CardView by lazy { findViewById(R.id.cv_item_pelayanan_hukum_gratis) }
    private val cardTindakanHukumLain: CardView by lazy { findViewById(R.id.cv_item_tindakan_hukum_lain) }
    private val cardChat: CardView by lazy { findViewById(R.id.cv_chat) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_perdata_dan_tata_usaha_negara)

        setUpUi()
    }

    private fun setUpUi() {
        val chatPopUp = ChatPopUp()

        imgBack.setOnClickListener { finish() }
        cardLayananPermohonanMoU.setOnClickListener {
            startActivity(Intent(applicationContext, PermohonanMouActivity::class.java))
        }
        cardPelayananBantuanHukum.setOnClickListener {
            startActivity(Intent(applicationContext, BantuanHukumActivity::class.java))
        }
        cardPelayananPertimbanganHukum.setOnClickListener {
            startActivity(Intent(applicationContext, PertimbanganHukumActivity::class.java))
        }
        cardPelayananHukumGratis.setOnClickListener {
            startActivity(Intent(applicationContext, HukumGratisActivity::class.java))
        }
        cardTindakanHukumLain.setOnClickListener {
            startActivity(Intent(applicationContext, HukumLainActivity::class.java))
        }

        cardChat.setOnClickListener {
            chatPopUp.show(supportFragmentManager, "Chat pop-up")
        }

    }
}