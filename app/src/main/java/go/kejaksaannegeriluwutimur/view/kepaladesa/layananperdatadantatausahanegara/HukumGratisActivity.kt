package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import android.Manifest
import android.app.Activity
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
import go.kejaksaannegeriluwutimur.util.Constants.HUKUM_GRATIS
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.SuccessPopUp
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.layananperdatadantatausahanegara.HukumGratisDanLainViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@AndroidEntryPoint
class HukumGratisActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val hukumGratisDanLainViewModel: HukumGratisDanLainViewModel by viewModels()
    private val successPopUp = SuccessPopUp()

    private val type = HUKUM_GRATIS
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

                when (whichFile) {
                    listFile[0] -> {
                        if (documentFile!!.exists()) {
                            val reqBodyDokumen: RequestBody = inputStream.let {
                                it!!.readBytes().toRequestBody("*/*".toMediaTypeOrNull(), 0)
                            }
                            partDokumenTerkait = MultipartBody.Part.createFormData(
                                "dokumen", "File", reqBodyDokumen
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
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Gagal pilih file",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                    listFile[1] -> {
                        if (documentFile!!.exists()) {
                            val reqBodyKtp: RequestBody = inputStream.let {
                                it!!.readBytes().toRequestBody("*/*".toMediaTypeOrNull(), 0)
                            }
                            partKtp = MultipartBody.Part.createFormData(
                                "ktp", "File", reqBodyKtp
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
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Gagal pilih file",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

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
        setContentView(R.layout.activity_hukum_gratis)

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

        btnPilihFileDokumenTerkait.setOnClickListener {
            checkPermission(listFile[0])
        }
        btnPilihFileKtp.setOnClickListener {
            checkPermission(listFile[1])
        }
        btnKirimData.setOnClickListener {
            if (!isBtnLoading && checkNullFields()) {
                if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
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
                        sp.getInt(Constants.PREF_USER_ID, 0).toString(),
                        sp.getString(Constants.PREF_USER_TOKEN, null).toString(),
                    )
                } else {
                    Toast.makeText(applicationContext, "Email tidak valid", Toast.LENGTH_SHORT)
                        .show()
                }
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
                btnKirimData.setShowProgress(false, "Kirim Data")
                setFieldEnabled(true)
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
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