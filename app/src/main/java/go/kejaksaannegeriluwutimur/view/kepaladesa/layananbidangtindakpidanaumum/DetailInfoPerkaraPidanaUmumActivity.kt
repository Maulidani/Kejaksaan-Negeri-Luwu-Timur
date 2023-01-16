package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import go.kejaksaannegeriluwutimur.R

class DetailInfoPerkaraPidanaUmumActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val tvPenyidik: TextView by lazy { findViewById(R.id.tv_nama_penyidik) }
    private val tvTersangka: TextView by lazy { findViewById(R.id.tv_nama_tersangka) }
    private val tvTanggalP16: TextView by lazy { findViewById(R.id.tv_tgl_p_16) }
    private val tvTanggalTerimaBerkas: TextView by lazy { findViewById(R.id.tv_tgl_terima_berkas) }
    private val tvTanggalSpdp: TextView by lazy { findViewById(R.id.tv_spdp) }
    private val tvPasal: TextView by lazy { findViewById(R.id.tv_pasal) }
    private val tvTanggalP16A: TextView by lazy { findViewById(R.id.tv_tgl_p_16_a) }
    private val tvKeterangan: TextView by lazy { findViewById(R.id.tv_keterangan) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info_perkara_pidana_umum)

        val intentPenyidik = intent.getStringExtra("penyidik")
        val intentTersangka = intent.getStringExtra("tersangka")
        val intentTanggalP16 = intent.getStringExtra("tanggal_p_16")
        val intentTanggalTerimaBerkas = intent.getStringExtra("tanggal_terima_berkas")
        val intentTanggalSpdp = intent.getStringExtra("tanggal_spdp")
        val intentPasal = intent.getStringExtra("pasal")
        val intentTanggalP16A = intent.getStringExtra("tanggal_p_16_a")
        val intentKeterangan = intent.getStringExtra("keterangan")

        tvPenyidik.text = intentPenyidik.toString()
        tvTersangka.text = intentTersangka.toString()
        tvTanggalP16.text = intentTanggalP16.toString()
        tvTanggalTerimaBerkas.text = intentTanggalTerimaBerkas.toString()
        tvTanggalSpdp.text = intentTanggalSpdp.toString()
        tvPasal.text = intentPasal.toString()
        tvTanggalP16A.text = intentTanggalP16A.toString()
        tvKeterangan.text = intentKeterangan.toString()

        imgBack.setOnClickListener { finish() }
    }
}