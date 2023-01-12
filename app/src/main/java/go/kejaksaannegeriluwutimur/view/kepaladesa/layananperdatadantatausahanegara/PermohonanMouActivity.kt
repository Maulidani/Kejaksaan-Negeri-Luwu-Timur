package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
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
import go.kejaksaannegeriluwutimur.viewmodel.layananperdatadantatausahanegara.PermohonanMouViewModel
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
class PermohonanMouActivity : AppCompatActivity() {
    private val permohonanMouViewModel: PermohonanMouViewModel by viewModels()

    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val etInstansiPemerintahan: EditText by lazy { findViewById(R.id.et_instansi_pemerintahan) }
    private val etNamaKegiatan: EditText by lazy { findViewById(R.id.et_nama_kegiatan) }
    private val etNilaiKegiatan: EditText by lazy { findViewById(R.id.et_nilai_kegiatan) }
    private val etJadwalSosialisasiPresentasiPermasalahan: EditText by lazy { findViewById(R.id.et_jadwal_sosialisasi_presentasi_permasalahan) }
    private val etNamaAliranDanKegiatan: EditText by lazy { findViewById(R.id.et_nama_aliran_dan_kegiatan) }
    private val etNamaPenanggungJawab: EditText by lazy { findViewById(R.id.et_nama_penanggung_jawab) }
    private val etTeleponInstansi: EditText by lazy { findViewById(R.id.et_telepon_instansi) }
    private val etEmailInstansi: EditText by lazy { findViewById(R.id.et_email_instansi) }
    private val etFilePermohonanMou: EditText by lazy { findViewById(R.id.et_file_permohonan_mou) }
    private val etUploadKtp: EditText by lazy { findViewById(R.id.et_upload_ktp) }
    private val btnKirimPermohonan: MaterialButton by lazy { findViewById(R.id.btn_kirim_permohonan) }
    private var isBtnLoading = false
    private var partFilePermohonan: MultipartBody.Part? = null
    private var partKtp: MultipartBody.Part? = null
    private val listFile = arrayOf("file-permohonan", "ktp")
    private var whichFile = ""

    // Initialize result launcher
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Initialize result data
            val data: Intent? = result.data
            // check condition
            if (data != null) {
                // When data is not equal to empty
                // Get File uri
                val sUri: Uri? = data.data
                // Get File path
                val sPath: String? = sUri?.path

                when (whichFile) {
                    listFile[0] -> {
                        val file = File(sPath!!)
                        val reqBodyFilePermohonan: RequestBody =
                            file.asRequestBody("*/*".toMediaTypeOrNull())
                        partFilePermohonan = MultipartBody.Part.createFormData(
                            "param-name", file.name, reqBodyFilePermohonan
                        )
                        etFilePermohonanMou.setText(file.name)
                        etFilePermohonanMou.setTextColor(getColor(R.color.green_40))
                        whichFile = ""
                        Log.d(application.toString(), "Pick file : File permohonan : uri : $sUri")
                        Log.d(
                            application.toString(),
                            "Pick file : File permohonan : uri path : $sPath"
                        )
                        Log.d(
                            application.toString(),
                            "Pick file : File permohonan : file name : ${file.name}"
                        )
                    }
                    listFile[1] -> {
                        val file = File(sPath!!)
                        val reqBodyKtp: RequestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
                        partKtp = MultipartBody.Part.createFormData(
                            "param-name", file.name, reqBodyKtp
                        )
                        etUploadKtp.setText(file.name)
                        etUploadKtp.setTextColor(getColor(R.color.green_40))
                        whichFile = ""
                        Log.d(application.toString(), "Pick file : Ktp : uri : $sUri")
                        Log.d(application.toString(), "Pick file : Ktp : uri path : $sPath")
                        Log.d(application.toString(), "Pick file : Ktp : file name : ${file.name}")
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

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }

        etFilePermohonanMou.setOnClickListener {
            checkPermission(listFile[0])
        }

        etUploadKtp.setOnClickListener {
            checkPermission(listFile[1])
        }

        btnKirimPermohonan.setOnClickListener {
            if (!isBtnLoading && checkNullFields()) {
                permohonanMouViewModel.kirimPermohonanMou(
                    etInstansiPemerintahan.text.toString(),
                    etNamaKegiatan.text.toString(),
                    etNilaiKegiatan.text.toString(),
                    etJadwalSosialisasiPresentasiPermasalahan.text.toString(),
                    etNamaAliranDanKegiatan.text.toString(),
                    etNamaPenanggungJawab.text.toString(),
                    etTeleponInstansi.text.toString(),
                    etEmailInstansi.text.toString(),
                    "",
                    partFilePermohonan!!,
                    partKtp!!,
                    "token",
                )

            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
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
            }
            is ScreenState.Error -> {
//                isBtnLoading = false
//                btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
//                setFieldEnabled(true)
//                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    isBtnLoading = false
                    btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
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
            etJadwalSosialisasiPresentasiPermasalahan.text.isNullOrEmpty() -> {
                etJadwalSosialisasiPresentasiPermasalahan.setError(
                    "Tidak boleh kosong", null
                )
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
            partFilePermohonan != null -> {
                etFilePermohonanMou.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            partKtp != null -> {
                etUploadKtp.setError(
                    "Tidak boleh kosong", null
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setFieldEnabled(isEnabled: Boolean) {
        etInstansiPemerintahan.isEnabled = isEnabled
        etNamaKegiatan.isEnabled = isEnabled
        etNilaiKegiatan.isEnabled = isEnabled
        etJadwalSosialisasiPresentasiPermasalahan.isEnabled = isEnabled
        etNamaAliranDanKegiatan.isEnabled = isEnabled
        etNamaPenanggungJawab.isEnabled = isEnabled
        etTeleponInstansi.isEnabled = isEnabled
        etEmailInstansi.isEnabled = isEnabled
        etFilePermohonanMou.isEnabled = isEnabled
        etUploadKtp.isEnabled = isEnabled
    }

    private fun checkPermission(listFile: String) {
        // check condition
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // When permission is not granted
            // Result permission
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        } else {
            // When permission is granted
            // Create method
            whichFile = listFile
            selectFile()
        }
    }

    private fun selectFile() {
        // Initialize intent
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // set type
        intent.type = "*/*"
        // Launch intent
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // check condition
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            selectFile()
        } else {
            // When permission is denied
            // Display toast
            Toast.makeText(
                applicationContext, "Permission Denied", Toast.LENGTH_SHORT
            ).show()
        }
    }
}