package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara.*

class LayananBidangIntelijenActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val cardPakem: CardView by lazy { findViewById(R.id.cv_item_pakem) }
    private val cardPengawasanBarangCetakanDanPerbukuan: CardView by lazy { findViewById(R.id.cv_item_pengawasan_barang_cetakan_dan_perbukuan) }
    private val cardChat: CardView by lazy { findViewById(R.id.cv_chat) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_bidang_intelijen)

        setOnClick()

    }

    private fun setOnClick() {

    imgBack.setOnClickListener { finish() }

    cardPakem.setOnClickListener {
        startActivity(Intent(applicationContext, PakemActivity::class.java))
    }
    cardPengawasanBarangCetakanDanPerbukuan.setOnClickListener {
        startActivity(Intent(applicationContext, PengawasanBarangCetakanDanPembukuanActivity::class.java))
    }

    cardChat.setOnClickListener {
        //
    }

    }
}