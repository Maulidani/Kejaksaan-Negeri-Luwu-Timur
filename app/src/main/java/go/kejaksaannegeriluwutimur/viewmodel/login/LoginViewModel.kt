package go.kejaksaannegeriluwutimur.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _dataResponse = MutableLiveData<ScreenState<Model.Response>>()

    val dataResponse: LiveData<ScreenState<Model.Response>> get() = _dataResponse

    fun login(username: String, password: String) {
        _dataResponse.postValue(ScreenState.Loading(null))

        val client = repository.postLoginUser(username, password)
        client.enqueue(object : Callback<Model.Response> {
            override fun onResponse(
                call: Call<Model.Response>,
                response: Response<Model.Response>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _dataResponse.postValue(ScreenState.Success(response.body()!!))
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
