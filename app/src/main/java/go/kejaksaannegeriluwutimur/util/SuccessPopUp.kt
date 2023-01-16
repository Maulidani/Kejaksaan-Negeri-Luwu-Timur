package go.kejaksaannegeriluwutimur.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import go.kejaksaannegeriluwutimur.R

class SuccessPopUp : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.popup_success, container, false)
        dialog?.setTitle("Alert success")

        val btnBack = rootView.findViewById<MaterialButton>(R.id.btn_back)

        if (isAdded) {
            btnBack.setOnClickListener {
                activity?.finish()
            }
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val window: Window? = dialog!!.window
        dialog!!.setCancelable(false)

        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
