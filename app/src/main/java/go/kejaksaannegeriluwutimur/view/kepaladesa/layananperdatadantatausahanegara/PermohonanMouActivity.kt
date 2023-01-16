package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Patterns.EMAIL_ADDRESS
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
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_ID
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_TOKEN
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.SuccessPopUp
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.layananperdatadantatausahanegara.PermohonanMouViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PermohonanMouActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val permohonanMouViewModel: PermohonanMouViewModel by viewModels()
    private val successPopUp = SuccessPopUp()

    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val etInstansiPemerintahan: EditText by lazy { findViewById(R.id.et_instansi_pemerintahan) }
    private val etNamaKegiatan: EditText by lazy { findViewById(R.id.et_nama_kegiatan) }
    private val etNilaiKegiatan: EditText by lazy { findViewById(R.id.et_nilai_kegiatan) }
    private val imgDateJadwalSosialisasiPresentasiPermasalahan: ImageView by lazy { findViewById(R.id.iv_date_jadwal_sosialisasi_presentasi_permasalahan) }
    private val tvJadwalSosialisasiPresentasiPermasalahan: TextView by lazy { findViewById(R.id.tv_jadwal_sosialisasi_presentasi_permasalahan) }
    private val etNamaAliranDanKegiatan: EditText by lazy { findViewById(R.id.et_nama_aliran_dan_kegiatan) }
    private val etNamaPenanggungJawab: EditText by lazy { findViewById(R.id.et_nama_penanggung_jawab) }
    private val etTeleponInstansi: EditText by lazy { findViewById(R.id.et_telepon_instansi) }
    private val etEmailInstansi: EditText by lazy { findViewById(R.id.et_email_instansi) }
    private val btnPilihFilePermohonan: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_permohonan) }
    private val btnPilihFileKtp: MaterialButton by lazy { findViewById(R.id.btn_pilih_file_ktp) }
    private val tvKeteranganFilePermohonan: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_permohonan) }
    private val tvKeteranganFileKtp: TextView by lazy { findViewById(R.id.tv_keterangan_form_file_ktp) }
    private val btnKirimPermohonan: MaterialButton by lazy { findViewById(R.id.btn_kirim_permohonan) }
    private var isBtnLoading = false
    private var partFilePermohonan: MultipartBody.Part? = null
    private var partKtp: MultipartBody.Part? = null
    private val listFile = arrayOf("file-permohonan", "ktp")
    private var whichFile = ""
    private var sJadwalSosialisasiPresentasiPermasalahan = ""

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
                            val reqBodyFilePermohonan: RequestBody = inputStream.let {
                                it!!.readBytes().toRequestBody("*/*".toMediaTypeOrNull(), 0)
                            }
                            partFilePermohonan = MultipartBody.Part.createFormData(
                                "file_permohonan", "file", reqBodyFilePermohonan
                            )
                            sUri.let {
                                contentResolver.query(it, null, null, null, null)
                            }?.use {
                                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                                it.moveToFirst()
                                val fileInformation =
                                    it.getString(nameIndex) + " | " + it.getString(sizeIndex) + " kb"
                                tvKeteranganFilePermohonan.text = fileInformation
                                tvKeteranganFilePermohonan.setTextColor(getColor(R.color.green_40))
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
        setContentView(R.layout.activity_permohonan_mou)

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

        imgDateJadwalSosialisasiPresentasiPermasalahan.setOnClickListener {
            datePicker(tvJadwalSosialisasiPresentasiPermasalahan)
        }
        tvJadwalSosialisasiPresentasiPermasalahan.setOnClickListener {
            datePicker(tvJadwalSosialisasiPresentasiPermasalahan)
        }
        btnPilihFilePermohonan.setOnClickListener {
            checkPermission(listFile[0])
        }
        btnPilihFileKtp.setOnClickListener {
            checkPermission(listFile[1])
        }
        btnKirimPermohonan.setOnClickListener {

            if (!isBtnLoading && checkNullFields()) {
                if (EMAIL_ADDRESS.matcher(etEmailInstansi.text.toString()).matches()) {
                    permohonanMouViewModel.kirimPermohonanMou(
                        etInstansiPemerintahan.text.toString(),
                        etNamaKegiatan.text.toString(),
                        etNilaiKegiatan.text.toString(),
                        sJadwalSosialisasiPresentasiPermasalahan,
                        etNamaAliranDanKegiatan.text.toString(),
                        etNamaPenanggungJawab.text.toString(),
                        etTeleponInstansi.text.toString(),
                        etEmailInstansi.text.toString(),
                        sp.getInt(PREF_USER_ID, 0).toString(),
                        partFilePermohonan!!,
                        partKtp!!,
                        sp.getString(PREF_USER_TOKEN, null).toString(),
                    )
                } else {
                    Toast.makeText(applicationContext, "Email tidak valid", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setUpObservers() {
        permohonanMouViewModel.dataResponse.observe(this) {
            processKirimPermohonan(it)
        }
    }

    private fun processKirimPermohonan(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                isBtnLoading = true
                btnKirimPermohonan.setShowProgress(true, null)
                setFieldEnabled(false)
            }
            is ScreenState.Success -> {
                isBtnLoading = false
                btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
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
                btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
                setFieldEnabled(true)
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkNullFields(): Boolean {
        return when {
            etInstansiPemerintahan.text.isNullOrEmpty() -> {
                etInstansiPemerintahan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etNamaKegiatan.text.isNullOrEmpty() -> {
                etNamaKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etNilaiKegiatan.text.isNullOrEmpty() -> {
                etNilaiKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            tvJadwalSosialisasiPresentasiPermasalahan.text.isNullOrEmpty() -> {
                false
            }
            etNamaAliranDanKegiatan.text.isNullOrEmpty() -> {
                etNamaAliranDanKegiatan.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etNamaPenanggungJawab.text.isNullOrEmpty() -> {
                etNamaPenanggungJawab.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etTeleponInstansi.text.isNullOrEmpty() -> {
                etTeleponInstansi.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            etEmailInstansi.text.isNullOrEmpty() -> {
                etEmailInstansi.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            partFilePermohonan == null -> {
                false
            }
            partKtp == null -> {
                false
            }
            sJadwalSosialisasiPresentasiPermasalahan == "" -> {
                false
            }
            else -> {
                true
            }
        }
    }

    private fun datePicker(field: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, yearTahun, monthOfYear, dayOfMonth ->
                val sField = ("$dayOfMonth/" + monthOfYear + 1 + "/$yearTahun")
                field.text = sField
                val sDate = ("$yearTahun/" + monthOfYear + 1 + "/$dayOfMonth")
                sJadwalSosialisasiPresentasiPermasalahan = sDate
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun setFieldEnabled(isEnabled: Boolean) {
        etInstansiPemerintahan.isEnabled = isEnabled
        etNamaKegiatan.isEnabled = isEnabled
        etNilaiKegiatan.isEnabled = isEnabled
        etNamaAliranDanKegiatan.isEnabled = isEnabled
        etNamaPenanggungJawab.isEnabled = isEnabled
        etTeleponInstansi.isEnabled = isEnabled
        etEmailInstansi.isEnabled = isEnabled
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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
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