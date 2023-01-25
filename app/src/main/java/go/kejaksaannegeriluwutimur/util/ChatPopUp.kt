package go.kejaksaannegeriluwutimur.util

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.gustavoavila.websocketclient.WebSocketClient
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.adapter.AdapterMessage
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants.MSG_TERJADI_KESALAHAN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_TOKEN
import go.kejaksaannegeriluwutimur.util.Constants.RESPONSE_TOKEN_SALAH
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.chat.MessageChatViewModel
import go.kejaksaannegeriluwutimur.viewmodel.chat.UserChatViewModel
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject

@AndroidEntryPoint
class ChatPopUp : DialogFragment() {
    @Inject
    lateinit var sp: SharedPreferences
    private val chatViewModel: UserChatViewModel by viewModels()
    private val messageChatViewModel: MessageChatViewModel by viewModels()

    private var wsClient: WebSocketClient? = null
    private var isConnected = false

    private lateinit var adapterMessage: AdapterMessage
    private lateinit var rvMessageChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSendMessage: ImageView
    private lateinit var srLoading: SwipeRefreshLayout

    private var roomId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.popup_chat, container, false)
        dialog?.setTitle("Chat admin")

        rvMessageChat = rootView.findViewById<RecyclerView>(R.id.rv_message)
        etMessage = rootView.findViewById<EditText>(R.id.et_message)
        btnSendMessage = rootView.findViewById<ImageView>(R.id.iv_send_message)
        srLoading = rootView.findViewById<SwipeRefreshLayout>(R.id.sr_loading)

        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (isAdded) {
            srLoading.isRefreshing = true
            createWsClient()
            if (isConnected()) {
                createWsClient()
            }

            btnSendMessage.setOnClickListener {
                if (etMessage.text.isNotEmpty() && roomId != "") {
                    messageChatViewModel.kirimPesan(
                        roomId,
                        etMessage.text.toString(),
                        sp.getString(PREF_USER_TOKEN, null).toString()
                    )
                }
            }

            srLoading.setOnRefreshListener {
                srLoading.isRefreshing = true

                if (roomId != "") {
                    messageChatViewModel.loadChat(
                        roomId,
                        sp.getString(PREF_USER_TOKEN, null).toString()
                    )
                } else {
                    chatViewModel.buatRoom(
                        ROLE_KEPALA_DESA,
                        "0",
                        sp.getString(PREF_USER_TOKEN, null).toString()
                    )
                }
            }

            if (!isLogin && userRole != ROLE_KEPALA_DESA) {
                Toast.makeText(
                    requireActivity(),
                    MSG_TERJADI_KESALAHAN,
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                dismiss()
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            setUpObservers()

            if (roomId != "") {
                messageChatViewModel.loadChat(
                    roomId,
                    sp.getString(PREF_USER_TOKEN, null).toString()
                )
            } else {
                chatViewModel.buatRoom(
                    ROLE_KEPALA_DESA,
                    "0",
                    sp.getString(PREF_USER_TOKEN, null).toString()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val window: Window? = dialog!!.window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onStop() {
        super.onStop()

        closeConnected()
    }


    private fun setUpObservers() {
        chatViewModel.dataResponse.observe(requireActivity()) {
            processBuatRoom(it)
        }

        messageChatViewModel.dataArrayResponse.observe(this) {
            processLoadChat(it)
        }
        messageChatViewModel.dataResponse.observe(this) {
            processKirimPesan(it)
        }
    }

    private fun processBuatRoom(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                //
            }
            is ScreenState.Success -> {
                srLoading.isRefreshing = false
                if (state.data?.success == true) {
                    roomId = state.data.data.id.toString()
                    messageChatViewModel.loadChat(
                        roomId,
                        sp.getString(PREF_USER_TOKEN, null).toString()
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                if (state.data?.message == RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        requireContext(),
                        MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    sp.edit { clear() }
                    dismiss()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                dismiss()
            }
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
                        layoutManager = LinearLayoutManager(requireActivity()).apply {
                            stackFromEnd = true
                            reverseLayout = false
                        }
                    }
                    rvMessageChat.adapter = adapterMessage

                } else {
                    Toast.makeText(requireActivity(), "Tidak ada data chat", Toast.LENGTH_SHORT)
                        .show()
                }

                if (state.data.message == RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        requireActivity(),
                        MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    ).show()
                    sp.edit { clear() }
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    dismiss()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false
                Toast.makeText(requireActivity(), state.message, Toast.LENGTH_SHORT).show()
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
                    roomId,
                    sp.getString(PREF_USER_TOKEN, null).toString()
                )

                if (state.data?.message == RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(
                        requireActivity(),
                        MSG_TERJADI_KESALAHAN,
                        Toast.LENGTH_SHORT
                    ).show()
                    sp.edit { clear() }
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    dismiss()
                }
            }
            is ScreenState.Error -> {
                etMessage.isEnabled = true
                Toast.makeText(requireActivity(), state.message, Toast.LENGTH_SHORT).show()
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
                        roomId,
                        sp.getString(PREF_USER_TOKEN, null).toString()
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
