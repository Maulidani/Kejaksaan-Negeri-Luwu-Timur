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
import go.kejaksaannegeriluwutimur.util.Constants.ROLE_ADMIN
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.util.Ui.setShowProgress
import go.kejaksaannegeriluwutimur.view.admin.AdminHomeActivity
import go.kejaksaannegeriluwutimur.viewmodel.login.LoginViewModel

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var btnLogin: MaterialButton
    private lateinit var inputUsername: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private var isBtnLoading = false

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
            setUpUi()
            setUpObservers()
        }
    }

    private fun setUpUi() {
        btnLogin = requireActivity().findViewById(R.id.btn_login_admin)
        inputUsername = requireActivity().findViewById(R.id.input_username)
        inputPassword = requireActivity().findViewById(R.id.input_password_admin)

        btnLogin.setOnClickListener {
            if (!isBtnLoading) {
                val username: String
                val password: String

                if (inputUsername.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Lengkapi data username", Toast.LENGTH_SHORT)
                        .show()
                } else if (inputPassword.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Lengkapi data password", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    username = inputUsername.text.toString()
                    password = inputPassword.text.toString()
                    loginViewModel.login(username, password)
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
                inputUsername.isEnabled = false
                inputPassword.isEnabled = false
            }
            is ScreenState.Success -> {
                if (state.data?.data?.roles == ROLE_ADMIN) {
                    startActivity(Intent(requireActivity(), AdminHomeActivity::class.java))
                    activity?.finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Username atau Password salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                isBtnLoading = false
                btnLogin.setShowProgress(false, "Login")
                inputUsername.isEnabled = true
                inputPassword.isEnabled = true
            }
            is ScreenState.Error -> {
                isBtnLoading = false
                btnLogin.setShowProgress(false, "Login")
                inputUsername.isEnabled = true
                inputPassword.isEnabled = true
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()

                // test
                if (state.message == ROLE_ADMIN) {
                    startActivity(Intent(requireActivity(), AdminHomeActivity::class.java))
                    activity?.finish()
                }
            }
        }

    }

}