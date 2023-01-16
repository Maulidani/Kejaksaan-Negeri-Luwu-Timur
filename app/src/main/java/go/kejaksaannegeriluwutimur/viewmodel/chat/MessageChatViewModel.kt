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
class MessageChatViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var _dataArrayResponse = MutableLiveData<ScreenState<Model.DataArrayResponse>>()
    val dataArrayResponse: LiveData<ScreenState<Model.DataArrayResponse>> get() = _dataArrayResponse

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()
    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun loadChat(
        roomId: String,
        token: String,
    ) {
        _dataArrayResponse.postValue(ScreenState.Loading(null))

        val client: Call<Model.DataArrayResponse>? =
            roomId.toIntOrNull()?.let { repository.getLoadChat(token, it) }
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

    fun kirimPesan(
        roomId: String,
        message: String,
        token: String,
    ) {
        _dataResponse.postValue(ScreenState.Loading(null))

        val client: Call<Model.Response>? =
            roomId.toIntOrNull()?.let { repository.postChat(it, message, token) }
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
                    _dataResponse.postValue(
                        ScreenState.Error(
                            response.code().toString(),
                            null
                        )
                    )
                }
            }

            override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }

}
