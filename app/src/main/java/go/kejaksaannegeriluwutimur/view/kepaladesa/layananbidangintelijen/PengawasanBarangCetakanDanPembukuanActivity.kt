package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.SuccessPopUp
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.layananbidangintelijen.PengawasanBarangCetakanDanPembukuanViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PengawasanBarangCetakanDanPembukuanActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val pengawasanBarangCetakanDanPembukuanViewModel: PengawasanBarangCetakanDanPembukuanViewModel by viewModels()
    private val successPopUp = SuccessPopUp()

    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val etNamaPelapor: EditText by lazy { findViewById(R.id.et_nama_lengkap) }
    private val etNomorHpWa: EditText by lazy { findViewById(R.id.et_nomor_hp_wa) }
    private val etEmail: EditText by lazy { findViewById(R.id.et_email) }
    private val etAlamat: EditText by lazy { findViewById(R.id.et_alamat_lengkap) }
    private val etjudulBukuCetakan: EditText by lazy { findViewById(R.id.et_judul_buku_cetakan) }
    private val etPenulisBukuCetakan: EditText by lazy { findViewById(R.id.et_penulis_buku_cetakan) }
    private val etBentuk: TextView by lazy { findViewById(R.id.et_bentuk) }
    private val imgDateTanggalTerbit: ImageView by lazy { findViewById(R.id.iv_date_tanggal_terbit) }
    private val tvTanggalTerbit: TextView by lazy { findViewById(R.id.tv_tanggal_terbit) }
    private val etIsiBuku: EditText by lazy { findViewById(R.id.et_isi_buku) }
    private val btnPilihFileDokumen: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_dokumen_terkait) }
    private val tvKeteranganFileDokumen: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_dokumen_terkait) }
    private val btnKirimLaporanPengaduan: MaterialButton by lazy { findViewById(R.id.btn_kirim_laporan_pengaduan) }
    private var isBtnLoading = false
    private var partFileDokumen: MultipartBody.Part? = null
    private var sTanggalTerbit = ""

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val sUri: Uri? = data.data
                val sPath: String? = sUri?.path

                sUri?.let {
                    try {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Gagal pilih file",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                val documentFile = sUri?.let { DocumentFile.fromSingleUri(applicationContext, it) }
                val fileUri = documentFile?.uri
                val path = fileUri?.path
                val inputStream = sUri?.let { contentResolver.openInputStream(it) }

                if (documentFile!!.exists()) {
                    val reqBodyFilePermohonan: RequestBody = inputStream.let {
                        it!!.readBytes().toRequestBody("*/*".toMediaTypeOrNull(), 0)
                    }
                    partFileDokumen = MultipartBody.Part.createFormData(
                        "dokumen", "File", reqBodyFilePermohonan
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
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Gagal pilih file",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        } else {
            Toast.makeText(applicationContext, "Gagal pilih file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengawasan_barang_cetakan_dan_pembukuan)

        setUpUi()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()

        isLogin()
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (!isLogin && userRole != Constants.ROLE_KEPALA_DESA) {
            Toast.makeText(applicationContext, Constants.MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }

        imgDateTanggalTerbit.setOnClickListener {
            datePicker(tvTanggalTerbit)
        }
        tvTanggalTerbit.setOnClickListener {
            datePicker(tvTanggalTerbit)
        }
        btnPilihFileDokumen.setOnClickListener {
            checkPermission()
        }
        btnKirimLaporanPengaduan.setOnClickListener {
            if (!isBtnLoading && checkNullFields()) {
                if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                    pengawasanBarangCetakanDanPembukuanViewModel.kirimLaporanPengaduan(
                        etNamaPelapor.text.toString(),
                        etNomorHpWa.text.toString(),
                        etEmail.text.toString(),
                        etAlamat.text.toString(),
                        etjudulBukuCetakan.text.toString(),
                        etPenulisBukuCetakan.text.toString(),
                        etBentuk.text.toString(),
                        sTanggalTerbit,
                        etIsiBuku.text.toString(),
                        partFileDokumen!!,
                        sp.getString(Constants.PREF_USER_TOKEN, null).toString(),
                    )
                } else {
                    Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpObservers() {
        pengawasanBarangCetakanDanPembukuanViewModel.dataResponse.observe(this) {
            processKirimLaporanPengaduan(it)
        }
    }

    private fun processKirimLaporanPengaduan(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                isBtnLoading = true
                btnKirimLaporanPengaduan.setShowProgress(true, null)
                setFieldEnabled(false)
            }
            is ScreenState.Success -> {
                isBtnLoading = false
                btnKirimLaporanPengaduan.setShowProgress(false, "Kirim Laporan Pengaduan")
                setFieldEnabled(true)
                successPopUp.show(supportFragmentManager, "Chat pop-up")

                if (state.data?.message == Constants.RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        applicationContext,
                        Constants.MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    ).show()
                    sp.edit { clear() }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            }
            is ScreenState.Error -> {
                isBtnLoading = false
                btnKirimLaporanPengaduan.setShowProgress(false, "Kirim Laporan Pengaduan")
                setFieldEnabled(true)
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
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
            etjudulBukuCetakan.text.isNullOrEmpty() -> {
                etjudulBukuCetakan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etPenulisBukuCetakan.text.isNullOrEmpty() -> {
                etPenulisBukuCetakan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etBentuk.text.isNullOrEmpty() -> {
                etBentuk.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            tvTanggalTerbit.text.isNullOrEmpty() -> {
                false
            }
            etIsiBuku.text.isNullOrEmpty() -> {
                etIsiBuku.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            partFileDokumen == null -> {
                false
            }
            sTanggalTerbit == "" -> {
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
        etjudulBukuCetakan.isEnabled = isEnabled
        etPenulisBukuCetakan.isEnabled = isEnabled
        etBentuk.isEnabled = isEnabled
        etIsiBuku.isEnabled = isEnabled
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

    private fun datePicker(field: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearTahun, monthOfYear, dayOfMonth ->
                val sField = ("$dayOfMonth/" + monthOfYear + 1 + "/$yearTahun")
                field.text = sField
                val sDate = ("$yearTahun/" + monthOfYear + 1 + "/$dayOfMonth")
                sTanggalTerbit = sDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
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