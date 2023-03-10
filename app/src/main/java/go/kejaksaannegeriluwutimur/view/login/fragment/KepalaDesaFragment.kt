package go.kejaksaannegeriluwutimur.view.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_KEPALA_DESA
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.kepaladesa.HomeActivity
import go.kejaksaannegeriluwutimur.viewmodel.login.LoginViewModel

@AndroidEntryPoint
class KepalaDesaFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var btnLogin: MaterialButton
    private lateinit var inputNik: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private var isBtnLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kepala_desa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            setUpUi()
            setUpObservers()
        }
    }

    private fun setUpUi() {
        btnLogin = requireActivity().findViewById(R.id.btn_login_kepala_desa)
        inputNik = requireActivity().findViewById(R.id.input_nik)
        inputPassword = requireActivity().findViewById(R.id.input_password_kepala_desa)

        btnLogin.setOnClickListener {
            if (!isBtnLoading) {
                val nik: String
                val password: String

                if (inputNik.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Lengkapi data nik", Toast.LENGTH_SHORT).show()
                } else if (inputPassword.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Lengkapi data password", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    nik = inputNik.text.toString()
                    password = inputPassword.text.toString()

                    loginViewModel.login(nik, password)
                }
            }
        }
    }

    private fun setUpObservers() {
        loginViewModel.dataResponse.observe(requireActivity()) {
            processLoginResponse(it)
        }
    }

    private fun processLoginResponse(state: ScreenState<Model.Response>) {
        when (state) {
            is ScreenState.Loading -> {
                isBtnLoading = true
                btnLogin.setShowProgress(true, null)
                inputNik.isEnabled = false
                inputPassword.isEnabled = false
            }
            is ScreenState.Success -> {
                if (state.data?.data?.roles == ROLE_KEPALA_DESA) {
                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                    activity?.finish()
                } else {
                    Toast.makeText(requireContext(), "Nik atau Password salah", Toast.LENGTH_SHORT)
                        .show()
                }
                isBtnLoading = false
                btnLogin.setShowProgress(false, "Login")
                inputNik.isEnabled = true
                inputPassword.isEnabled = true
            }
            is ScreenState.Error -> {
                isBtnLoading = false
                btnLogin.setShowProgress(false, "Login")
                inputNik.isEnabled = true
                inputPassword.isEnabled = true
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

}