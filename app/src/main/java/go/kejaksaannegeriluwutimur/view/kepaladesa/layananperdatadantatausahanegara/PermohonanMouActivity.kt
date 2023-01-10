package go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    private var isFilePermohonanMou = false
    private var isUploadKtp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permohonan_mou)

        setUpUi()
        setUpObservers()
    }

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }

        etFilePermohonanMou.setOnClickListener {
            Toast.makeText(applicationContext, "Memilih file", Toast.LENGTH_SHORT).show()
            isFilePermohonanMou = true
            etFilePermohonanMou.setText(getString(R.string.file_sudah_dipilih))
            etFilePermohonanMou.setTextColor(getColor(R.color.green_40))
            etFilePermohonanMou.isEnabled = true
        }

        etUploadKtp.setOnClickListener {
            Toast.makeText(applicationContext, "Memilih file", Toast.LENGTH_SHORT).show()
            isUploadKtp = true
            etUploadKtp.setText(getString(R.string.file_sudah_dipilih))
            etUploadKtp.setTextColor(getColor(R.color.green_40))
            etUploadKtp.isEnabled = true
        }

        btnKirimPermohonan.setOnClickListener {
            if (checkNullFields()) {
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
                    filePermohonan = MultipartBody.Part.createFormData(
                        "image",
                        File("path").name,
                        File("path").asRequestBody("file/*".toMediaTypeOrNull())
                    ),
                    ktp = MultipartBody.Part.createFormData(
                        "image",
                        File("path").name,
                        File("path").asRequestBody("file/*".toMediaTypeOrNull())
                    ),
                    btnKirimPermohonan.text.toString(),
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
                btnKirimPermohonan.setShowProgress(true, null)
                setFieldEnabled(false)
            }
            is ScreenState.Success -> {
                btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
                setFieldEnabled(true)
            }
            is ScreenState.Error -> {
//                btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
//                setFieldEnabled(true)
//                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    btnKirimPermohonan.setShowProgress(false, "Kirim Permohonan")
                    setFieldEnabled(true)
                    startActivity(
                        Intent(applicationContext, AlertActivity::class.java).putExtra(
                            "name",
                            ""
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
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etNamaKegiatan.text.isNullOrEmpty() -> {
                etNamaKegiatan.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etNilaiKegiatan.text.isNullOrEmpty() -> {
                etNilaiKegiatan.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etJadwalSosialisasiPresentasiPermasalahan.text.isNullOrEmpty() -> {
                etJadwalSosialisasiPresentasiPermasalahan.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etNamaAliranDanKegiatan.text.isNullOrEmpty() -> {
                etNamaAliranDanKegiatan.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etNamaPenanggungJawab.text.isNullOrEmpty() -> {
                etNamaPenanggungJawab.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etTeleponInstansi.text.isNullOrEmpty() -> {
                etTeleponInstansi.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            etEmailInstansi.text.isNullOrEmpty() -> {
                etEmailInstansi.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            !isFilePermohonanMou -> {
                etFilePermohonanMou.setError(
                    "Tidak boleh kosong",
                    null
                )
                false
            }
            !isUploadKtp -> {
                etUploadKtp.setError(
                    "Tidak boleh kosong",
                    null
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
        btnKirimPermohonan.isEnabled = isEnabled
    }
}