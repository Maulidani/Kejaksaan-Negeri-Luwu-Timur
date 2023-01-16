package go.kejaksaannegeriluwutimur.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdapterSisfoTilang(
    private val list: ArrayList<Model.DataResponse>,
) :
    RecyclerView.Adapter<AdapterSisfoTilang.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNamaOperasi: TextView by lazy { itemView.findViewById(R.id.tv_nama_operasi) }
        private val tvTempatOperasi: TextView by lazy { itemView.findViewById(R.id.tv_tempat_operasi) }
        private val tvWaktuOperasi: TextView by lazy { itemView.findViewById(R.id.tv_waktu_operasi) }
        private val item: CardView by lazy { itemView.findViewById(R.id.cv_sisfo_tilang) }

        @SuppressLint("SetTextI18n")
        fun bindData(listItem: Model.DataResponse) {
            val date = LocalDate.parse(listItem.waktu_operasi)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            val formattedDate = date.format(formatter)

            tvNamaOperasi.text = "Nama = ${listItem.nama_operasi}"
            tvTempatOperasi.text = "Tempat = ${listItem.tempat_operasi}"
            tvWaktuOperasi.text = "Tanggal = $formattedDate"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sisfo_tilang, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}
