package go.kejaksaannegeriluwutimur.viewmodel.layananbidangintelijen

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
class PakemViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()

    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun kirimPengaduan(
        namaPelapor: String,
        nomorHpWa: String,
        email: String,
        alamat: String,
        namaAliranKegiatan: String,
        tempatKegiatan: String,
        keteranganKegiatan: String,
        partDokumen: MultipartBody.Part,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        val partNamaPelapor: RequestBody =
            namaPelapor.toRequestBody("text/plain".toMediaTypeOrNull())
        val partNomorHpWa: RequestBody =
            nomorHpWa.toRequestBody("text/plain".toMediaTypeOrNull())
        val partEmail: RequestBody =
            email.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAlamat: RequestBody =
            alamat.toRequestBody("text/plain".toMediaTypeOrNull())
        val partNamaAliranKegiatan: RequestBody =
            namaAliranKegiatan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partNamaTempatKegiatan: RequestBody =
            tempatKegiatan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partKeteranganKegiatan: RequestBody =
            keteranganKegiatan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partToken: RequestBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

        val client = repository.postPakem(
            partNamaPelapor,
            partNomorHpWa,
            partEmail,
            partAlamat,
            partNamaAliranKegiatan,
            partNamaTempatKegiatan,
            partKeteranganKegiatan,
            partDokumen,
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
