package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen.PakemActivity
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen.PengawasanBarangCetakanDanPembukuanActivity

class LayananBidangTindakPidanaUmumActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val cardSistemInformasiTilang: CardView by lazy { findViewById(R.id.cv_item_sistem_informasi_tilang) }
    private val cardInfoPerkaraPidanaUmum: CardView by lazy { findViewById(R.id.cv_item_info_perkara_pidana_umum) }
    private val cardJadwalPemeriksaan: CardView by lazy { findViewById(R.id.cv_item_jadwal_pemeriksaan) }
    private val cardChat: CardView by lazy { findViewById(R.id.cv_chat) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_bidang_tindak_pidana_umum)

        setOnClick()

    }

    private fun setOnClick() {

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
            //
        }

    }

}