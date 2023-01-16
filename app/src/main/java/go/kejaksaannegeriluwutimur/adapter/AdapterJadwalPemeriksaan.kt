package go.kejaksaannegeriluwutimur.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import go.kejaksaannegeriluwutimur.R
import go.kejaksaannegeriluwutimur.model.Model
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum.DetailJadwalPemeriksaanActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdapterJadwalPemeriksaan(
    private val list: ArrayList<Model.DataResponse>,
) :
    RecyclerView.Adapter<AdapterJadwalPemeriksaan.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTgl: TextView by lazy { itemView.findViewById(R.id.tv_tanggal) }
        private val tvKasus: TextView by lazy { itemView.findViewById(R.id.tv_kasus) }
        private val tvStatus: TextView by lazy { itemView.findViewById(R.id.tv_status) }
        private val item: CardView by lazy { itemView.findViewById(R.id.cv_jadwal_pemeriksaan) }

        @SuppressLint("SetTextI18n")
        fun bindData(listItem: Model.DataResponse) {
            val date = LocalDate.parse(listItem.tanggal)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            val formattedDate = date.format(formatter)

            tvTgl.text = "Tanggal : $formattedDate"
            tvKasus.text = "Kasus : ${listItem.kasus}"
            tvStatus.text = "status : ${listItem.status}"

            item.setOnClickListener {
                ContextCompat.startActivity(
                    itemView.context,
                    Intent(
                        itemView.context,
                        DetailJadwalPemeriksaanActivity::class.java
                    )
                        .putExtra("tanggal", listItem.tanggal)
                        .putExtra("tanggal_spdp", listItem.tgl_spdp)
                        .putExtra("kasus", listItem.kasus)
                        .putExtra("status", listItem.status)
                        .putExtra("tahap_status", listItem.tahap_kasus), null
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_jadwal_pemeriksaan, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}
