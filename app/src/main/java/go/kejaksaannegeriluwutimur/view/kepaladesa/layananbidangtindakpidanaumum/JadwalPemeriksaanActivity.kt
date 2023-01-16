package go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.adapter.AdapterJadwalPemeriksaan
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.util.Constants
import go.kejaksaannegeriluwutimur.util.Constants.JADWAL_PEMERIKSAAN
import go.kejaksaannegeriluwutimur.util.ScreenState
import go.kejaksaannegeriluwutimur.view.login.LoginActivity
import go.kejaksaannegeriluwutimur.viewmodel.layananbidangtindakpidanaumum.AllLayananTindakPidanaUmumViewModel
import javax.inject.Inject

@AndroidEntryPoint
class JadwalPemeriksaanActivity : AppCompatActivity() {
    @Inject
    lateinit var sp: SharedPreferences
    private val jadwalPemeriksaanViewModel: AllLayananTindakPidanaUmumViewModel by viewModels()
    private val imgBack: ImageView by lazy { findViewById(R.id.iv_back) }
    private val rvJadwalPemeriksaan: RecyclerView by lazy { findViewById(R.id.rv_jadwal_pemeriksaan) }
    private val pbLoading: ProgressBar by lazy { findViewById(R.id.pb_loading) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal_pemeriksaan)

        setUpUi()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()

        isLogin()
    }

    private fun isLogin() {
        val isLogin = sp.getBoolean(Constants.PREF_USER_IS_LOGIN, false)
        val userToken = sp.getString(Constants.PREF_USER_TOKEN, null)
        val userRole = sp.getString(Constants.PREF_USER_ROLE, null)

        if (!isLogin && userRole != Constants.ROLE_KEPALA_DESA && userToken != null) {
            Toast.makeText(applicationContext, Constants.MSG_TERJADI_KESALAHAN, Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        } else {
            jadwalPemeriksaanViewModel.getData(JADWAL_PEMERIKSAAN, userToken!!)

        }
    }

    private fun setUpUi() {
        imgBack.setOnClickListener { finish() }
    }

    private fun setUpObservers() {
        jadwalPemeriksaanViewModel.dataResponse.observe(this) {
            processGetData(it)
        }
    }

    private fun processGetData(state: ScreenState<Model.DataArrayResponse>) {
        when (state) {
            is ScreenState.Loading -> {
                pbLoading.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
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
                if (state.data?.data?.size == 0) {
                    Toast.makeText(applicationContext, "Tidak ada data", Toast.LENGTH_SHORT).show()
                } else {
                    val adapter = state.data?.let { AdapterJadwalPemeriksaan(it.data) }
                    rvJadwalPemeriksaan.layoutManager = LinearLayoutManager(applicationContext)
                    rvJadwalPemeriksaan.adapter = adapter
                }
                pbLoading.visibility = View.GONE
            }
            is ScreenState.Error -> {
                Toast.makeText(applicationContext, state.message, Toast.LENGTH_SHORT).show()
                pbLoading.visibility = View.GONE
            }
        }
    }
}