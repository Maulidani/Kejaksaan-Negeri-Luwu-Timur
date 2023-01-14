package go.kejaksaannegeriluwutimur.util

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class ChatPopUp : DialogFragment() {
    @Inject
    lateinit var sp: SharedPreferences

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

        val btnSendMessage = rootView.findViewById<ImageView>(R.id.iv_send_message)

        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (isAdded) {
            btnSendMessage?.setOnClickListener {
                Toast.makeText(requireActivity(), "kirim", Toast.LENGTH_SHORT).show()
            }

            if (!isLogin && userRole != Constants.ROLE_KEPALA_DESA) {
                Toast.makeText(
                    requireActivity(),
                    Constants.MSG_TERJADI_KESALAHAN,
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
            }
        }

        return rootView
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
}
