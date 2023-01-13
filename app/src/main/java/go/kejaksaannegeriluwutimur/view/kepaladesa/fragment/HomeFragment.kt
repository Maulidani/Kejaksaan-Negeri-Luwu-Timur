package go.kejaksaannegeriluwutimur.view.kepaladesa.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.util.LogoutPopUp
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangintelijen.LayananBidangIntelijenActivity
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum.LayananBidangTindakPidanaUmumActivity
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananperdatadantatausahanegara.LayananPerdataDanTataUsahaNegaraActivity

class HomeFragment : Fragment() {
    private lateinit var imgLogout: ImageView
    private lateinit var cardLayananPerdataDanTataUsahaNegara: CardView
    private lateinit var cardLayananBidangIntelijen: CardView
    private lateinit var cardLayananBidangTindakPidanaUmum: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            setUpUi()
        }
    }

    private fun setUpUi() {
        val logoutPopUp = LogoutPopUp()

        imgLogout = requireActivity().findViewById(R.id.iv_logout)

        cardLayananPerdataDanTataUsahaNegara =
            requireActivity().findViewById(R.id.cv_layanan_pardata_dan_tata_usaha_negara)
        cardLayananBidangIntelijen =
            requireActivity().findViewById(R.id.cv_layanan_bidang_intelijen)
        cardLayananBidangTindakPidanaUmum =
            requireActivity().findViewById(R.id.cv_layanan_bidang_tindak_pidana_umum)

        cardLayananPerdataDanTataUsahaNegara.setOnClickListener {
            startActivity(Intent(requireActivity(), LayananPerdataDanTataUsahaNegaraActivity::class.java))
        }
        cardLayananBidangIntelijen.setOnClickListener {
            startActivity(Intent(requireActivity(), LayananBidangIntelijenActivity::class.java))
        }
        cardLayananBidangTindakPidanaUmum.setOnClickListener {
            startActivity(Intent(requireActivity(), LayananBidangTindakPidanaUmumActivity::class.java))
        }

        imgLogout.setOnClickListener {
            logoutPopUp.show(requireActivity().supportFragmentManager, "Logout pop-up")
        }
    }

}