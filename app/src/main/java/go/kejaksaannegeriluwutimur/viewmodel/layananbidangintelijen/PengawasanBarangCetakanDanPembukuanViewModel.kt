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
class PengawasanBarangCetakanDanPembukuanViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()

    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun kirimLaporanPengaduan(
        namaPelapor: String,
        nomorHpWa: String,
        email: String,
        alamatLengkap: String,
        judulBukuCetakan: String,
        penulisBukuCetakan: String,
        bentuk: String,
        tanggalTerbit: String,
        isiBuku: String,
        partFileDokumen: MultipartBody.Part,
        token: String
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))


        val partNamaPelapor: RequestBody =
            namaPelapor.toRequestBody("text/plain".toMediaTypeOrNull())
        val partNomorHpWa: RequestBody = nomorHpWa.toRequestBody("text/plain".toMediaTypeOrNull())
        val partEmail: RequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAlamatLengkap: RequestBody =
            alamatLengkap.toRequestBody("text/plain".toMediaTypeOrNull())
        val partJudulBukuCetakan: RequestBody =
            judulBukuCetakan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPenulisBukuCetakan: RequestBody =
            penulisBukuCetakan.toRequestBody("text/plain".toMediaTypeOrNull())
        val partBentuk: RequestBody = bentuk.toRequestBody("text/plain".toMediaTypeOrNull())
        val partTanggalTerbit: RequestBody =
            tanggalTerbit.toRequestBody("text/plain".toMediaTypeOrNull())
        val partIsiBuku: RequestBody = isiBuku.toRequestBody("text/plain".toMediaTypeOrNull())
        val partToken: RequestBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

        val client = repository.postPengawasanBarangCetakanPembukuan(
            partNamaPelapor,
            partNomorHpWa,
            partEmail,
            partAlamatLengkap,
            partJudulBukuCetakan,
            partPenulisBukuCetakan,
            partBentuk,
            partTanggalTerbit,
            partIsiBuku,
            partFileDokumen,
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
