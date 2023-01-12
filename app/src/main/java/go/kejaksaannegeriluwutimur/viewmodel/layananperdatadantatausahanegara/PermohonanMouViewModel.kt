package go.kejaksaannegeriluwutimur.viewmodel.layananperdatadantatausahanegara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.ScreenState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PermohonanMouViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()

    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun kirimPermohonanMou(
        instansiPemerintahan: String,
        namaKegiatan: String,
        nilaiKegiatan: String,
        jadwalSosialisasi: String,
        namaAliran: String,
        namaPenanggung: String,
        teleponInstansi: String,
        emailInstansi: String,
        userId: String,
        partFilePermohonan: MultipartBody.Part,
        partKtp: MultipartBody.Part,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        val partInstansiPemerintahan: RequestBody =
            instansiPemerintahan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partnamaKegiatan: RequestBody =
            namaKegiatan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partnilaiKegiatan: RequestBody =
            nilaiKegiatan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partjadwalSosialisasi: RequestBody =
            jadwalSosialisasi.toRequestBody("text/plain".toMediaTypeOrNull())
        val partnamaAliran: RequestBody = namaAliran.toRequestBody("text/plain".toMediaTypeOrNull())
        val partnamaPenanggung: RequestBody =
            namaPenanggung.toRequestBody("text/plain".toMediaTypeOrNull())
        val partteleponInstansi: RequestBody =
            teleponInstansi.toRequestBody("text/plain".toMediaTypeOrNull())
        val partemailInstansi: RequestBody =
            emailInstansi.toRequestBody("text/plain".toMediaTypeOrNull())
        val partuserId: RequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val partToken: RequestBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

        val client = repository.postPermohonanMou(
            partInstansiPemerintahan,
            partnamaKegiatan,
            partnilaiKegiatan,
            partjadwalSosialisasi,
            partnamaAliran,
            partnamaPenanggung,
            partteleponInstansi,
            partemailInstansi,
            partuserId,
            partFilePermohonan,
            partKtp,
            partToken
        )
        client.enqueue(object : Callback<Model.Response> {
            override fun onResponse(
                call: Call<Model.Response>,
                response: Response<Model.Response>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.success) {
                        _dataResponse.postValue(ScreenState.Success(response.body()!!))
                    } else {
                        _dataResponse.postValue(ScreenState.Error(response.body()!!.message, null))
                    }
                } else {
                    _dataResponse.postValue(ScreenState.Error(response.code().toString(), null))
                }
            }

            override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }

}
