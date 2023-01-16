package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.ChatPopUp

@AndroidEntryPoint
class LayananBidangTindakPidanaUmumActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val cardSistemInformasiTilang: CardView by lazy { findViewById(R.id.cv_item_sistem_informasi_tilang) }
    private val cardInfoPerkaraPidanaUmum: CardView by lazy { findViewById(R.id.cv_item_info_perkara_pidana_umum) }
    private val cardJadwalPemeriksaan: CardView by lazy { findViewById(R.id.cv_item_jadwal_pemeriksaan) }
    private val cardChat: CardView by lazy { findViewById(R.id.cv_chat) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_bidang_tindak_pidana_umum)

        setUpUI()
    }

    private fun setUpUI() {
        val chatPopUp = ChatPopUp()

        imgBack.setOnClickListener { finish() }

        cardSistemInformasiTilang.setOnClickListener {
            startActivity(Intent(applicationContext, SistemInformasiTilangActivity::class.java))
        }
        cardInfoPerkaraPidanaUmum.setOnClickListener {
            startActivity(Intent(applicationContext, InfoPerkaraPidanaUmumActivity::class.java))
        }
        cardJadwalPemeriksaan.setOnClickListener {
            startActivity(Intent(applicationContext, JadwalPemeriksaanActivity::class.java))
        }

        cardChat.setOnClickListener {
            chatPopUp.show(supportFragmentManager, "Chat pop-up")
        }

    }

}