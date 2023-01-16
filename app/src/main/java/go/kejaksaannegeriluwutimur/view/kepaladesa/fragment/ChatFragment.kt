package go.kejaksaannegeriluwutimur.view.kepaladesa.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants.MSG_TERJADI_KESALAHAN
import go.kejaksaannegeriluwutimur.util.Constants.PREF_USER_TOKEN
import go.kejaksaannegeriluwutimur.util.Constants.RESPONSE_TOKEN_SALAH
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.view.kepaladesa.ChatContentsActivity
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.chat.UserChatViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    @Inject
    lateinit var sp: SharedPreferences
    private val chatViewModel: UserChatViewModel by viewModels()

    private lateinit var cardChatAdmin: CardView
    private lateinit var imgChatAdmin: ImageView
    private lateinit var tvNameChatAdmin: TextView
    private lateinit var tvMessageChatAdmin: TextView
    private lateinit var tvTimeChatAdmin: TextView
    private lateinit var tvCountUnreadChatAdmin: TextView
    private lateinit var pbLoading: ProgressBar
    private lateinit var srLoading: SwipeRefreshLayout

    private var roomId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            setUpUi()
            setUpObservers()

            chatViewModel.getData(
                ROLE_KEPALA_DESA,
                sp.getString(PREF_USER_TOKEN, null).toString(), ""
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (isAdded) {
            chatViewModel.getData(
                ROLE_KEPALA_DESA,
                sp.getString(PREF_USER_TOKEN, null).toString(), ""
            )
        }
    }

    private fun setUpUi() {
        cardChatAdmin = requireActivity().findViewById(R.id.cv_chat_admin)
        imgChatAdmin = requireActivity().findViewById(R.id.iv_chat_user)
        tvNameChatAdmin = requireActivity().findViewById(R.id.tv_chat_name)
        tvMessageChatAdmin = requireActivity().findViewById(R.id.tv_chat_message)
        tvTimeChatAdmin = requireActivity().findViewById(R.id.tv_chat_time)
        tvCountUnreadChatAdmin =
            requireActivity().findViewById(R.id.tv_chat_count_unread_messages)
        pbLoading = requireActivity().findViewById(R.id.pb_loading)
        srLoading = requireActivity().findViewById(R.id.sr_loading)

        tvTimeChatAdmin.visibility = View.INVISIBLE
        tvMessageChatAdmin.visibility = View.INVISIBLE
        tvCountUnreadChatAdmin.visibility = View.GONE

        imgChatAdmin.load("-") {
            crossfade(true)
            crossfade(500)
            placeholder(R.drawable.ic_account_chat)
            error(R.drawable.ic_account_chat)
            transformations(CircleCropTransformation())
        }

        cardChatAdmin.setOnClickListener {
            chatViewModel.buatRoom(
                ROLE_KEPALA_DESA,
                "0",
                sp.getString(PREF_USER_TOKEN, null).toString()
            )
        }

        srLoading.setOnRefreshListener {
            srLoading.isRefreshing = true

            chatViewModel.getData(
                ROLE_KEPALA_DESA,
                sp.getString(PREF_USER_TOKEN, null).toString(), ""
            )
        }
    }

    private fun setUpObservers() {
        chatViewModel.dataArrayResponse.observe(requireActivity()) {
            processGetUser(it)
        }
        chatViewModel.dataResponse.observe(requireActivity()) {
            processBuatRoom(it)
        }
    }

    private fun processGetUser(state: ScreenState<Model.DataArrayResponse>) {
        when (state) {
            is ScreenState.Loading -> {
                srLoading.isRefreshing = true
                tvNameChatAdmin.visibility = View.INVISIBLE
                tvCountUnreadChatAdmin.visibility = View.GONE
            }
            is ScreenState.Success -> {
                srLoading.isRefreshing = false
                tvNameChatAdmin.text = getString(R.string.admin_kejaksaan)
                tvNameChatAdmin.visibility = View.VISIBLE

                if (state.data?.data?.size != 0) {
                    if (state.data?.chat?.size != 0) {
                        roomId = state.data?.chat?.get(0)?.room_id.toString()
                        tvCountUnreadChatAdmin.text =
                            state.data?.chat?.get(0)?.tidak_dibaca.toString()
                        tvCountUnreadChatAdmin.visibility = View.VISIBLE
                    } else {
                        tvCountUnreadChatAdmin.visibility = View.GONE
                    }

                } else {
                    tvCountUnreadChatAdmin.visibility = View.GONE
                    Toast.makeText(requireContext(), MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                        .show()
                }

                if (state.data?.message == RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(requireContext(), MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    sp.edit { clear() }
                    if (isAdded) activity?.finish()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false
                tvNameChatAdmin.text = getString(R.string.admin_kejaksaan)
                tvNameChatAdmin.visibility = View.VISIBLE
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processBuatRoom(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                srLoading.isRefreshing = true
                tvNameChatAdmin.visibility = View.INVISIBLE
            }
            is ScreenState.Success -> {
                srLoading.isRefreshing = false
                tvNameChatAdmin.text = getString(R.string.admin_kejaksaan)
                tvNameChatAdmin.visibility = View.VISIBLE

                if (state.data?.success == true) {
                    startActivity(
                        Intent(requireActivity(), ChatContentsActivity::class.java)
                            .putExtra("room_id", state.data.data.id.toString())
                    )
                } else {
                    Toast.makeText(requireContext(), MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                        .show()
                }

                if (state.data?.message == RESPONSE_TOKEN_SALAH) {
                    Toast.makeText(requireContext(), MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    sp.edit { clear() }
                    if (isAdded) activity?.finish()
                }
            }
            is ScreenState.Error -> {
                srLoading.isRefreshing = false
                tvNameChatAdmin.text = getString(R.string.admin_kejaksaan)
                tvNameChatAdmin.visibility = View.VISIBLE
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}