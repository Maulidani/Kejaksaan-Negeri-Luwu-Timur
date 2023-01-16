package go.kejaksaannegeriluwutimur.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model

class AdapterMessage(
    private val data: ArrayList<Model.DataResponse>,
    private val userId: Int,
) : RecyclerView.Adapter<AdapterMessage.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvMessageReceived: TextView by lazy { itemView.findViewById(R.id.tv_message_received) }
        private val tvMessageSent: TextView by lazy { itemView.findViewById(R.id.tv_message_sent) }
        private val cardReceived: CardView by lazy { itemView.findViewById(R.id.cv_received) }
        private val cardSent: CardView by lazy { itemView.findViewById(R.id.cv_sent) }

        fun bindData(listItem: Model.DataResponse) {

            if (userId == listItem.user_id) {
                cardSent.visibility = View.VISIBLE
                cardReceived.visibility = View.GONE
                tvMessageSent.text = listItem.message
            } else {
                cardSent.visibility = View.GONE
                cardReceived.visibility = View.VISIBLE
                tvMessageReceived.text = listItem.message
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int = data.size

}