package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import go.kejaksaannegeriluwutimur.R

class DetailJadwalPemeriksaanActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val tvTanggal: TextView by lazy { findViewById(R.id.tv_tanggal) }
    private val tvTanggalSpdp: TextView by lazy { findViewById(R.id.tv_spdp) }
    private val tvKasus: TextView by lazy { findViewById(R.id.tv_kasus) }
    private val tvStatus: TextView by lazy { findViewById(R.id.tv_status) }
    private val tvTahapKasus: TextView by lazy { findViewById(R.id.tv_tahap_kasus) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_jadwal_pemeriksaan)

        val intentTanggal = intent.getStringExtra("tanggal")
        val intentTanggalSpdp = intent.getStringExtra("tanggal_spdp")
        val intentKasus = intent.getStringExtra("kasus")
        val intentStatus = intent.getStringExtra("status")
        val intentTahapStatus = intent.getStringExtra("tahap_status")

        tvTanggal.text = intentTanggal.toString()
        tvTanggalSpdp.text = intentTanggalSpdp.toString()
        tvKasus.text = intentKasus.toString()
        tvStatus.text = intentStatus.toString()
        tvTahapKasus.text = intentTahapStatus.toString()

        imgBack.setOnClickListener { finish() }
    }
}