package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.util.AlertActivity
import go.kejaksaannegeriluwutimur.viewmodel.layananbidangintelijen.PakemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class PakemActivity : AppCompatActivity() {
    private val pakemViewModel: PakemViewModel by viewModels()

    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val etNamaPelapor: EditText by lazy { findViewById(R.id.et_nama_lengkap_pelapor) }
    private val etNomorHpWa: EditText by lazy { findViewById(R.id.et_nomor_hp_wa) }
    private val etEmail: EditText by lazy { findViewById(R.id.et_email) }
    private val etAlamat: EditText by lazy { findViewById(R.id.et_alamat_lengkap) }
    private val etNamaAliranKegiatan: TextView by lazy { findViewById(R.id.et_nama_aliran_dan_kegiatan) }
    private val etTempatKegiatan: EditText by lazy { findViewById(R.id.et_tempat_kegiatan) }
    private val etKeteranganKegiatan: EditText by lazy { findViewById(R.id.et_keterangan_kegiatan) }
    private val btnPilihFileDokumen: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_dokumen_terkait) }
    private val tvKeteranganFileDokumen: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_dokumen_terkait) }
    private val btnKirimPengaduan: MaterialButton by lazy { findViewById(R.id.btn_kirim_pengaduan) }
    private var isBtnLoading = false
    private var partFileDokumen: MultipartBody.Part? = null

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val sUri: Uri? = data.data
                val sPath: String? = sUri?.path

                val file = File(sPath!!)
                val reqBodyFilePermohonan: RequestBody =
                    file.asRequestBody("*/*".toMediaTypeOrNull())
                partFileDokumen = MultipartBody.Part.createFormData(
                    "dokumen", file.name, reqBodyFilePermohonan
                )
                sUri.let {
                    contentResolver.query(it, null, null, null, null)
                }?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    it.moveToFirst()
                    val fileInformation =
                        it.getString(nameIndex) + " | " + it.getString(sizeIndex) + " kb"
                    tvKeteranganFileDokumen.text = fileInformation
                    tvKeteranganFileDokumen.setTextColor(getColor(R.color.green_40))
                }
            }

        } else {
            Toast.makeText(applicationContext, "Gagal pilih file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pakem)

        setUpUi()
        setUpObservers()
    }

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }

        btnPilihFileDokumen.setOnClickListener {
            checkPermission()
        }

        btnKirimPengaduan.setOnClickListener {
            if (!isBtnLoading && checkNullFields()) {
                pakemViewModel.kirimPengaduan(
                    etNamaPelapor.text.toString(),
                    etNomorHpWa.text.toString(),
                    etEmail.text.toString(),
                    etAlamat.text.toString(),
                    etNamaAliranKegiatan.text.toString(),
                    etTempatKegiatan.text.toString(),
                    etKeteranganKegiatan.text.toString(),
                    partFileDokumen!!,
                    "token",
                )
            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpObservers() {
        pakemViewModel.dataResponse.observe(this) {
            processKirimPengaduan(it)
        }
    }

    private fun processKirimPengaduan(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                isBtnLoading = true
                btnKirimPengaduan.setShowProgress(true, null)
                setFieldEnabled(false)
            }
            is ScreenState.Success -> {
                if (state.data?.message == "Token Kadaluwarsa") {
                    // logout
                }
                isBtnLoading = false
                btnKirimPengaduan.setShowProgress(false, "Kirim Pengaduan")
                setFieldEnabled(true)
            }
            is ScreenState.Error -> {
//                isBtnLoading = false
//                btnKirimPengaduan.setShowProgress(false, "Kirim Pengaduan")
//                setFieldEnabled(true)
//                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    isBtnLoading = false
                    btnKirimPengaduan.setShowProgress(false, "Kirim Pengaduan")
                    setFieldEnabled(true)
                    startActivity(
                        Intent(applicationContext, AlertActivity::class.java).putExtra(
                            "name", ""
                        )
                    )
                }

            }
        }
    }

    private fun checkNullFields(): Boolean {
        return when {
            etNamaPelapor.text.isNullOrEmpty() -> {
                etNamaPelapor.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etNomorHpWa.text.isNullOrEmpty() -> {
                etNomorHpWa.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etEmail.text.isNullOrEmpty() -> {
                etEmail.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etAlamat.text.isNullOrEmpty() -> {
                etAlamat.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etNamaAliranKegiatan.text.isNullOrEmpty() -> {
                etNamaAliranKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etTempatKegiatan.text.isNullOrEmpty() -> {
                etTempatKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etKeteranganKegiatan.text.isNullOrEmpty() -> {
                etKeteranganKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            partFileDokumen == null -> {
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setFieldEnabled(isEnabled: Boolean) {
        etNamaPelapor.isEnabled = isEnabled
        etNomorHpWa.isEnabled = isEnabled
        etEmail.isEnabled = isEnabled
        etAlamat.isEnabled = isEnabled
        etNamaAliranKegiatan.isEnabled = isEnabled
        etTempatKegiatan.isEnabled = isEnabled
        etKeteranganKegiatan.isEnabled = isEnabled
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        } else {
            selectFile()
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile()
        } else {
            Toast.makeText(
                applicationContext, "Akses file tidak diizinkan", Toast.LENGTH_SHORT
            ).show()
        }
    }

}
