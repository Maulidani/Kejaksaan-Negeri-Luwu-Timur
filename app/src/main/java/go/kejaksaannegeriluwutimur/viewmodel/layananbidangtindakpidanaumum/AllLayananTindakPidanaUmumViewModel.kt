package go.kejaksaannegeriluwutimur.viewmodel.layananbidangtindakpidanaumum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.Constants.JADWAL_PEMERIKSAAN
import go.kejaksaannegeriluwutimur.util.Constants.PERKARA_PIDANA_UMUM
import go.kejaksaannegeriluwutimur.util.Constants.SISFO_TILANG
import go.kejaksaannegeriluwutimur.util.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AllLayananTindakPidanaUmumViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.DataArrayResponse>>()

    val dataResponse: LiveData<ScreenState<Model.DataArrayResponse>> get() = _dataResponse

    fun getData(
        type: String,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        var client: Call<Model.DataArrayResponse>? = null
        when (type) {
            SISFO_TILANG -> client = repository.getSisfoTilang(token)
            PERKARA_PIDANA_UMUM -> client = repository.getPerkaraPidana(token)
            JADWAL_PEMERIKSAAN -> client = repository.getJadwalPemeriksaan(token)
            else -> _dataResponse.postValue(
                ScreenState.Error(
                    Constants.MSG_TERJADI_KESALAHAN,
                    null
                )
            )
        }

        client?.enqueue(object : Callback<Model.DataArrayResponse> {
            override fun onResponse(
                call: Call<Model.DataArrayResponse>,
                response: Response<Model.DataArrayResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.message == Constants.RESPONSE_TOKEN_SALAH) {
                        _dataResponse.postValue(ScreenState.Success(response.body()!!))
                    } else {
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
                    }
                } else {
                    _dataResponse.postValue(ScreenState.Error(response.code().toString(), null))
                }
            }

            override fun onFailure(call: Call<Model.DataArrayResponse>, t: Throwable) {
                _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }

}
