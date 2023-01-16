package go.kejaksaannegeriluwutimur.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.repository.Repository
import go.kejaksaannegeriluwutimur.util.Constants.MSG_TERJADI_KESALAHAN
import go.kejaksaannegeriluwutimur.util.Constants.RESPONSE_TOKEN_SALAH
import go.kejaksaannegeriluwutimur.view.admin.AdminChatContentsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterUserChat(
    private val data: ArrayList<Model.DataResponse>,
    private val dataChat: ArrayList<Model.Chat>,
    private val token: String,
    private val repository: Repository
) : RecyclerView.Adapter<AdapterUserChat.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivPhotoUser: ImageView by lazy { itemView.findViewById(R.id.iv_user) }
        private val tvName: TextView by lazy { itemView.findViewById(R.id.tv_name) }
        private val tvMessage: TextView by lazy { itemView.findViewById(R.id.tv_message) }
        private val tvTime: TextView by lazy { itemView.findViewById(R.id.tv_time) }
        private val tvCountUnread: TextView by lazy { itemView.findViewById(R.id.tv_count_unread_messages) }
        private val item: CardView by lazy { itemView.findViewById(R.id.cv_chat) }

        fun bindData(listItem: Model.DataResponse) {

            if (dataChat.isNotEmpty()) {
                for (i in dataChat) {
                    if (listItem.id == i.id_lawan_chat) {
                        tvCountUnread.text = i.tidak_dibaca.toString()
                        tvCountUnread.visibility = View.VISIBLE

                    } else {
                        tvCountUnread.visibility = View.INVISIBLE
                    }
                }
            } else {
                tvCountUnread.visibility = View.INVISIBLE
            }

            tvName.text = listItem.username
            tvMessage.visibility = View.INVISIBLE
            tvTime.visibility = View.INVISIBLE

            item.setOnClickListener {
                val client = repository.postRoom(token, listItem.id)
                client.enqueue(object : Callback<Model.Response> {
                    override fun onResponse(
                        call: Call<Model.Response>,
                        response: Response<Model.Response>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.message == RESPONSE_TOKEN_SALAH) {
                                Toast.makeText(
                                    itemView.context,
                                    MSG_TERJADI_KESALAHAN,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Toast.makeText(
                                    itemView.context,
                                    "logout dan login ulang untuk memulai kembali",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (response.body()!!.success) {
                                    ContextCompat.startActivity(
                                        itemView.context,
                                        Intent(
                                            itemView.context,
                                            AdminChatContentsActivity::class.java
                                        )
                                            .putExtra("room_id", listItem.id.toString())
                                            .putExtra("username", listItem.username), null
                                    )
                                } else {
                                    Toast.makeText(
                                        itemView.context,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                itemView.context,
                                response.code().toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Model.Response>, t: Throwable) {
                        Toast.makeText(
                            itemView.context,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int = data.size

}