package go.kejaksaannegeriluwutimur.viewmodel.login

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.Constants.MSG_TERJADI_KESALAHAN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_ID
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_IS_LOGIN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_ROLE
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_TOKEN
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_ADMIN
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.util.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository, private val sp: SharedPreferences
) : ViewModel() {

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

                    if (response.body()!!.success) {
                        val responseUserRole = response.body()!!.data.roles
                        val responseUserId = response.body()!!.data.id
                        val responseUserToken = response.body()!!.data.token

                        sp.edit {
                            putInt(PREF_USER_ID, responseUserId)
                            putString(PREF_USER_TOKEN, responseUserToken)
                            putBoolean(PREF_USER_IS_LOGIN, true)
                        }

                        when (responseUserRole) {
                            ROLE_KEPALA_DESA -> {
                                sp.edit {
                                    putString(PREF_USER_ROLE, responseUserRole)
                                }
                            }
                            ROLE_ADMIN -> {
                                sp.edit {
                                    putString(PREF_USER_ROLE, responseUserRole)
                                }
                            }
                            else -> {
                                _dataResponse.postValue(
                                    ScreenState.Error(
                                        MSG_TERJADI_KESALAHAN,
                                        null
                                    )
                                )
                                sp.edit { clear() }
                            }
                        }
                    } else {
                        _dataResponse.postValue(ScreenState.Error(response.body()!!.message, null))
                    }

                } else {
                    _dataResponse.postValue(ScreenState.Error(response.code().toString(), null))
                }
            }

            override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                _dataResponse.postValue(ScreenState.Error(t.message.toString(), null))

                // test
                sp.edit {
                    putString(PREF_USER_ROLE, "user")
                    putInt(PREF_USER_ID, 1)
                    putString(PREF_USER_TOKEN, "responseUserToken")
                    putBoolean(PREF_USER_IS_LOGIN, true)
                }
            }
        })
    }

}
