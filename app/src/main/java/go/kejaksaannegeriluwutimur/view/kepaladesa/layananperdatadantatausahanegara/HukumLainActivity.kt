package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

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
import go.kejaksaannegeriluwutimur.viewmodel.layananperdatadantatausahanegara.HukumGratisDanLainViewModel
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
class HukumLainActivity : AppCompatActivity() {
    private val hukumGratisDanLainViewModel: HukumGratisDanLainViewModel by viewModels()

    private val type = "hukum lain"
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val etNamaLengkap: EditText by lazy { findViewById(R.id.et_nama_lengkap) }
    private val etAlamat: EditText by lazy { findViewById(R.id.et_alamat) }
    private val etNomorHpWa: EditText by lazy { findViewById(R.id.et_nomor_hp_wa) }
    private val etEmail: EditText by lazy { findViewById(R.id.et_email) }
    private val etKategori: EditText by lazy { findViewById(R.id.et_kategori) }
    private val etBentukPermasalahan: EditText by lazy { findViewById(R.id.et_bentuk_permasalahan_hukum) }
    private val etDetailPermasalahan: EditText by lazy { findViewById(R.id.et_detail_permasalahan) }
    private val btnPilihFileDokumenTerkait: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_dokumen_terkait) }
    private val btnPilihFileKtp: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_ktp) }
    private val tvKeteranganFileDokumenTerkait: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_dokumen_terkait) }
    private val tvKeteranganFileKtp: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_ktp) }
    private val btnKirimData: MaterialButton by lazy { findViewById(R.id.btn_kirim_data) }
    private var isBtnLoading = false
    private var partDokumenTerkait: MultipartBody.Part? = null
    private var partKtp: MultipartBody.Part? = null
    private val listFile = arrayOf("dokumen-terkait", "ktp")
    private var whichFile = ""

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val sUri: Uri? = data.data
                val sPath: String? = sUri?.path

                when (whichFile) {
                    listFile[0] -> {
                        val file = File(sPath!!)
                        val reqBodyFilePermohonan: RequestBody =
                            file.asRequestBody("*/*".toMediaTypeOrNull())
                        partDokumenTerkait = MultipartBody.Part.createFormData(
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
                            tvKeteranganFileDokumenTerkait.text = fileInformation
                            tvKeteranganFileDokumenTerkait.setTextColor(getColor(R.color.green_40))
                        }
                        whichFile = ""
                    }
                    listFile[1] -> {
                        val file = File(sPath!!)
                        val reqBodyKtp: RequestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
                        partKtp = MultipartBody.Part.createFormData(
                            "ktp", file.name, reqBodyKtp
                        )
                        sUri.let {
                            contentResolver.query(it, null, null, null, null)
                        }?.use {
                            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                            it.moveToFirst()
                            val fileInformation =
                                it.getString(nameIndex) + " | " + it.getString(sizeIndex) + " kb"
                            tvKeteranganFileKtp.text = fileInformation
                            tvKeteranganFileKtp.setTextColor(getColor(R.color.green_40))
                        }
                        whichFile = ""
                    }
                    else -> {
                        whichFile = ""
                        Toast.makeText(applicationContext, "Gagal pilih file", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        } else {
            whichFile = ""
            Toast.makeText(applicationContext, "Gagal pilih file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hukum_lain)

        setUpUi()
        setUpObservers()
    }

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }

        btnPilihFileDokumenTerkait.setOnClickListener {
            checkPermission(listFile[0])
        }
        btnPilihFileKtp.setOnClickListener {
            checkPermission(listFile[1])
        }
        btnKirimData.setOnClickListener {
            if (!isBtnLoading && checkNullFields()) {
                hukumGratisDanLainViewModel.kirimData(
                    type,
                    etNamaLengkap.text.toString(),
                    etAlamat.text.toString(),
                    etNomorHpWa.text.toString(),
                    etEmail.text.toString(),
                    etKategori.text.toString(),
                    etBentukPermasalahan.text.toString(),
                    etDetailPermasalahan.text.toString(),
                    partDokumenTerkait!!,
                    partKtp!!,
                    "",
                    "token",
                )
            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpObservers() {
        hukumGratisDanLainViewModel.dataResponse.observe(this) {
            processKirimData(it)
        }
    }

    private fun processKirimData(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                isBtnLoading = true
                btnKirimData.setShowProgress(true, null)
                setFieldEnabled(false)
            }
            is ScreenState.Success -> {
                isBtnLoading = false
                btnKirimData.setShowProgress(false, "Kirim Data")
                setFieldEnabled(true)
            }
            is ScreenState.Error -> {
//                isBtnLoading = false
//                btnKirimData.setShowProgress(false, "Kirim Data")
//                setFieldEnabled(true)
//                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    isBtnLoading = false
                    btnKirimData.setShowProgress(false, "Kirim Data")
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
            etNamaLengkap.text.isNullOrEmpty() -> {
                etNamaLengkap.setError(
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
            etKategori.text.isNullOrEmpty() -> {
                etKategori.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etBentukPermasalahan.text.isNullOrEmpty() -> {
                etBentukPermasalahan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etDetailPermasalahan.text.isNullOrEmpty() -> {
                etDetailPermasalahan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            partDokumenTerkait == null -> {
                false
            }
            partKtp == null -> {
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setFieldEnabled(isEnabled: Boolean) {
        etNamaLengkap.isEnabled = isEnabled
        etAlamat.isEnabled = isEnabled
        etNomorHpWa.isEnabled = isEnabled
        etEmail.isEnabled = isEnabled
        etKategori.isEnabled = isEnabled
        etBentukPermasalahan.isEnabled = isEnabled
        etDetailPermasalahan.isEnabled = isEnabled
    }

    private fun checkPermission(listFile: String) {
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
            whichFile = listFile
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