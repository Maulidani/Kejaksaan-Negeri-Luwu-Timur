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
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class LogoutPopUp : DialogFragment() {
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
        val rootView: View = inflater.inflate(R.layout.popup_logout, container, false)
        dialog?.setTitle("Chat admin")

        val tvTidak = rootView.findViewById<TextView>(R.id.tv_tidak)
        val tvYa = rootView.findViewById<TextView>(R.id.tv_ya)

        if (isAdded) {
            tvTidak.setOnClickListener { dismiss() }
            tvYa.setOnClickListener {
                sp.edit { clear() }
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
                dismiss()
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
