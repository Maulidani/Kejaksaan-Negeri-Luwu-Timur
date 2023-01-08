package go.kejaksaannegeriluwutimur.view.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.admin.AdminHomeActivity
import go.kejaksaannegeriluwutimur.view.kepaladesa.HomeActivity

class AdminFragment : Fragment() {
    private lateinit var btnLogin: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            btnLogin = requireActivity().findViewById(R.id.btn_login_admin)

            btnLogin.setOnClickListener {
                startActivity(Intent(requireActivity(), AdminHomeActivity::class.java))
            }
        }

    }

}