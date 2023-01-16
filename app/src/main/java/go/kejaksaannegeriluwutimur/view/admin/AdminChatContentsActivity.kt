package go.kejaksaannegeriluwutimur.view.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.gustavoavila.websocketclient.WebSocketClient
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.adapter.AdapterMessage
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.chat.MessageChatViewModel
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject

@AndroidEntryPoint
class AdminChatContentsActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val messageChatViewModel: MessageChatViewModel by viewModels()

    private var wsClient: WebSocketClient? = null
    private var isConnected = false
    private lateinit var adapterMessage: AdapterMessage
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val tvUserName: TextView by lazy { findViewById(R.id.tv_user_name) }
    private val etMessage: EditText by lazy { findViewById(R.id.et_message) }
    private val imgSend: ImageView by lazy { findViewById(R.id.iv_send_message) }
    private val rvMessageChat: RecyclerView by lazy { findViewById(R.id.rv_message) }
    private val srLoading: SwipeRefreshLayout by lazy { findViewById(R.id.sr_loading) }
    private var intentRoomId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_chat_contents)

        createWsClient()
        if (isConnected()) {
            createWsClient()
        }

        setUpUi()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()

        isLogin()
    }

    override fun onStop() {
        super.onStop()

        closeConnected()
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (!isLogin && userRole != Constants.ROLE_ADMIN) {
            Toast.makeText(applicationContext, Constants.MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpUi() {
        intentRoomId = intent.getStringExtra("room_id").toString()
        val intentName = intent.getStringExtra("username").toString()
        tvUserName.text = intentName

        srLoading.isRefreshing = true
        messageChatViewModel.loadChat(
            intentRoomId,
            sp.getString(Constants.PREF_USER_TOKEN, null).toString()
        )

        srLoading.setOnRefreshListener {
            srLoading.isRefreshing = true
            messageChatViewModel.loadChat(
                intentRoomId,
                sp.getString(Constants.PREF_USER_TOKEN, null).toString()
            )
        }

        imgBack.setOnClickListener { finish() }

        imgSend.setOnClickListener {
            if (etMessage.text.isNotEmpty()) {
                messageChatViewModel.kirimPesan(
                    intentRoomId,
                    etMessage.text.toString(),
                    sp.getString(Constants.PREF_USER_TOKEN, null).toString()
                )
            }
        }

    }

    private fun setUpObservers() {
        messageChatViewModel.dataArrayResponse.observe(this) {
            processLoadChat(it)
        }
        messageChatViewModel.dataResponse.observe(this) {
            processKirimPesan(it)
        }
    }


    private fun processLoadChat(state: ScreenState<Model.DataArrayResponse>) {
        when (state) {
            is ScreenState.Loading -> {
                //
            }
            is ScreenState.Success -> {
                srLoading.isRefreshing = false

                if (state.data?.data?.size != 0) {
                    adapterMessage =
                        AdapterMessage(state.data?.data!!, sp.getInt(Constants.PREF_USER_ID, 0))
                    rvMessageChat.apply {
                        layoutManager = LinearLayoutManager(applicationContext).apply {
                            stackFromEnd = true
                            reverseLayout = false
                        }
                    }
                    rvMessageChat.adapter = adapterMessage

                } else {
                    Toast.makeText(applicationContext, "Tidak ada data chat", Toast.LENGTH_SHORT)
                        .show()
                }

                if (state.data.message == Constants.RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        applicationContext,
                        Constants.MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    ).show()
                    sp.edit { clear() }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false

                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processKirimPesan(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                etMessage.isEnabled = false
            }
            is ScreenState.Success -> {
                etMessage.isEnabled = true
                etMessage.setText("")
                messageChatViewModel.loadChat(
                    intentRoomId,
                    sp.getString(Constants.PREF_USER_TOKEN, null).toString()
                )

                if (state.data?.message == Constants.RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        applicationContext,
                        Constants.MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    ).show()
                    sp.edit { clear() }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            }
            is ScreenState.Error -> {
                etMessage.isEnabled = true
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createWsClient() {
        if (isConnected) {
            return
        }

        try {
            val uri = URI(Constants.WEBSOCKET_URL)

            wsClient = object : WebSocketClient(uri) {

                override fun onOpen() {
                    isConnected = true
                    Log.d("WsClient", "WsClient onOpen: Opened")
                }

                override fun onTextReceived(message: String?) {
                    Log.d("WsClient", "WsClient onTextReceived: $message")

                    messageChatViewModel.loadChat(
                        intentRoomId,
                        sp.getString(Constants.PREF_USER_TOKEN, null).toString()
                    )
                }

                override fun onBinaryReceived(data: ByteArray?) {
                    Log.d("WsClient", "WsClient onBinaryReceived: $data")
                }

                override fun onPingReceived(data: ByteArray?) {
                    Log.d("WsClient", "WsClient onPingReceived: $data")
                }

                override fun onPongReceived(data: ByteArray?) {
                    Log.d("WsClient", "WsClient onPongReceived: $data")
                }

                override fun onException(e: Exception?) {
                    Log.d("WsClient", "WsClient onException: $e")
                }

                override fun onCloseReceived() {
                    isConnected = false
                    Log.d("WsClient", "WsClient onCloseReceived: Closed")
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }

        wsClient!!.setConnectTimeout(5000)
        wsClient!!.setReadTimeout(5000)
        wsClient!!.addHeader("Origin", Constants.BASE_URL)
        wsClient!!.enableAutomaticReconnection(2000)
        wsClient!!.connect()
    }

    private fun isConnected(): Boolean {
        return isConnected
    }

    private fun closeConnected() {
        wsClient?.close()
    }

}