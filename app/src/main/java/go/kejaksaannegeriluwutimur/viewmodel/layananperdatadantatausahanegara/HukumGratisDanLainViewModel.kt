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
class HukumGratisDanLainViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()

    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun kirimData(
        type: String,
        namaLengkap: String,
        alamat: String,
        nomorHpWa: String,
        email: String,
        kategori: String,
        bentukPermasalahan: String,
        detailPermasalahan: String,
        partDokumenTerkait: MultipartBody.Part,
        partKtp: MultipartBody.Part,
        userId: String,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        val partNamaLengkap: RequestBody =
            namaLengkap.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAlamat: RequestBody = alamat.toRequestBody("text/plain".toMediaTypeOrNull())
        val partNomorHpWa: RequestBody = nomorHpWa.toRequestBody("text/plain".toMediaTypeOrNull())
        val partEmail: RequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val partKategori: RequestBody = kategori.toRequestBody("text/plain".toMediaTypeOrNull())
        val partBentukPermasalahan: RequestBody =
            bentukPermasalahan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partDetailPermasalahan: RequestBody =
            detailPermasalahan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partuserId: RequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val partToken: RequestBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

        var client: Call<Model.Response>? = null
        when (type) {
            "hukum gratis" -> client = repository.postPelayananHukumGratis(
                partNamaLengkap,
                partAlamat,
                partNomorHpWa,
                partEmail,
                partKategori,
                partBentukPermasalahan,
                partDetailPermasalahan,
                partDokumenTerkait,
                partKtp,
                partuserId,
                partToken
            )
            "hukum lain" -> client = repository.postTindakanHukumLain(
                partNamaLengkap,
                partAlamat,
                partNomorHpWa,
                partEmail,
                partKategori,
                partBentukPermasalahan,
                partDetailPermasalahan,
                partDokumenTerkait,
                partKtp,
                partuserId,
                partToken
            )
            else -> _dataResponse.postValue(ScreenState.Error("Terjadi kesalahan", null))
        }

        if (client != null) {
            client.enqueue(object : Callback<Model.Response> {
                override fun onResponse(
                    call: Call<Model.Response>,
                    response: Response<Model.Response>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.success) {
                            _dataResponse.postValue(ScreenState.Success(response.body()!!))
                        } else {
                            _dataResponse.postValue(
                                ScreenState.Error(
                                    response.body()!!.message,
                                    null
                                )
                            )
                        }
                    } else {
                        _dataResponse.postValue(ScreenState.Error(response.code().toString(), null))
                    }
                }

                override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                    _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))
                }

            })

        } else {
            _dataResponse.postValue(ScreenState.Error("Terjadi kesalahan", null))
        }

    }

}
