package go.kejaksaannegeriluwutimur.view.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.adapter.AdapterUserChat
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_TOKEN
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_ADMIN
import go.kejaksaannegeriluwutimur.util.LogoutPopUp
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.chat.UserChatViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AdminHomeActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var repository: Repository

    private val chatViewModel: UserChatViewModel by viewModels()

    private lateinit var adapterUserChat: AdapterUserChat
    private val imgLogout: ImageView by lazy { findViewById(R.id.iv_logout) }
    private val rvUserChat: RecyclerView by lazy { findViewById(R.id.rv_user_chat) }
    private val pbLoading: ProgressBar by lazy { findViewById(R.id.pb_loading) }
    private val etSearch: EditText by lazy { findViewById(R.id.et_search_user) }
    private val btnSearch: MaterialButton by lazy { findViewById(R.id.btn_search) }
    private val srLoading: SwipeRefreshLayout by lazy { findViewById(R.id.sr_loading) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setUpUi()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()

        setUpUi()
        isLogin()

        chatViewModel.getData(
            ROLE_ADMIN,
            sp.getString(PREF_USER_TOKEN, null).toString(),
            "",
        )
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (!isLogin && userRole != ROLE_ADMIN) {
            Toast.makeText(applicationContext, Constants.MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpUi() {
        val logoutPopUp = LogoutPopUp()

        imgLogout.setOnClickListener {
            logoutPopUp.show(supportFragmentManager, "Logout pop-up")
        }

        btnSearch.setOnClickListener {
            if (etSearch.text.isNotEmpty()) {
                chatViewModel.getData(
                    ROLE_ADMIN,
                    sp.getString(PREF_USER_TOKEN, null).toString(), etSearch.text.toString()
                )
            }
        }
        srLoading.setOnRefreshListener {
            srLoading.isRefreshing = true

            chatViewModel.getData(
                ROLE_ADMIN,
                sp.getString(PREF_USER_TOKEN, null).toString(), ""
            )
        }
    }

    private fun setUpObservers() {
        chatViewModel.dataArrayResponse.observe(this) {
            processGetUser(it)
        }
    }

    private fun processGetUser(state: ScreenState<Model.DataArrayResponse>) {
        when (state) {
            is ScreenState.Loading -> {
                srLoading.isRefreshing = true
            }
            is ScreenState.Success -> {
                srLoading.isRefreshing = false
                if (state.data?.data?.size != 0) {

                    adapterUserChat = AdapterUserChat(
                        state.data?.data!!,
                        state.data.chat,
                        sp.getString(PREF_USER_TOKEN, null).toString(),
                        repository
                    )
                    rvUserChat.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                    }
                    rvUserChat.adapter = adapterUserChat

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Tidak ada data",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                if (state.data.message == Constants.RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        applicationContext,
                        Constants.MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    sp.edit { clear() }
                    finish()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}