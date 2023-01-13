package go.kejaksaannegeriluwutimur.view.kepaladesa.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.view.kepaladesa.ChatContentsActivity

class ChatFragment : Fragment() {
    private lateinit var cardChatAdmin: CardView
    private lateinit var imgChatAdmin: ImageView
    private lateinit var tvNameChatAdmin: TextView
    private lateinit var tvMessageChatAdmin: TextView
    private lateinit var tvTimeChatAdmin: TextView
    private lateinit var tvCountUnreadChatAdmin: TextView

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

        imgChatAdmin.load("-") {
            crossfade(true)
            crossfade(500)
            placeholder(R.drawable.ic_account_chat)
            error(R.drawable.ic_account_chat)
            transformations(CircleCropTransformation())
        }
        tvNameChatAdmin.text = getString(R.string.admin_kejaksaan)
        tvMessageChatAdmin.text = "-"
        tvTimeChatAdmin.text = "-"
        tvCountUnreadChatAdmin.text = "-"
        tvCountUnreadChatAdmin.visibility = View.GONE

        cardChatAdmin.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    ChatContentsActivity::class.java
                ).putExtra("name", tvNameChatAdmin.text.toString())
            )
        }

    }
}