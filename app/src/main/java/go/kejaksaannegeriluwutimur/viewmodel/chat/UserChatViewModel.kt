package go.kejaksaannegeriluwutimur.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var _dataArrayResponse = MutableLiveData<ScreenState<Model.DataArrayResponse>>()
    val dataArrayResponse: LiveData<ScreenState<Model.DataArrayResponse>> get() = _dataArrayResponse

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()
    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun getData(
        type: String,
        token: String,
        search: String,
    ) {
        _dataArrayResponse.postValue(ScreenState.Loading(null))

        var client: Call<Model.DataArrayResponse>? = null
        when (type) {
            Constants.ROLE_KEPALA_DESA -> client = repository.getListInKepalaDesa(token)
            Constants.ROLE_ADMIN -> client = repository.getListInAdmin(token, search)
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
                        _dataArrayResponse.postValue(ScreenState.Success(response.body()!!))
                    } else {
                        if (response.body()!!.success) {
                            _dataArrayResponse.postValue(ScreenState.Success(response.body()!!))
                        } else {
                            _dataArrayResponse.postValue(
                                ScreenState.Error(
                                    response.body()!!.message,
                                    null
                                )
                            )
                        }
                    }
                } else {
                    _dataArrayResponse.postValue(
                        ScreenState.Error(
                            response.code().toString(),
                            null
                        )
                    )
                }
            }

            override fun onFailure(call: Call<Model.DataArrayResponse>, t: Throwable) {
                _dataArrayResponse.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }

    fun buatRoom(
        type: String,
        idKepalaDesa: String,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        var client: Call<Model.Response>? = null
        when (type) {
            Constants.ROLE_KEPALA_DESA -> client = repository.postRoom(token, 0)
            Constants.ROLE_ADMIN -> client = repository.postRoom(token, idKepalaDesa.toIntOrNull())
            else -> _dataResponse.postValue(
                ScreenState.Error(
                    Constants.MSG_TERJADI_KESALAHAN,
                    null
                )
            )
        }

        client?.enqueue(object : Callback<Model.Response> {
            override fun onResponse(
                call: Call<Model.Response>,
                response: Response<Model.Response>
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

            override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }

}
