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
import go.kejaksaannegeriluwutimur.view.kepaladesa.layananbidangtindakpidanaumum.DetailInfoPerkaraPidanaUmumActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdapterPerkaraPidanaUmum(
    private val list: ArrayList<Model.DataResponse>,
) :
    RecyclerView.Adapter<AdapterPerkaraPidanaUmum.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNamaPenyidik: TextView by lazy { itemView.findViewById(R.id.tv_nama_penyidik) }
        private val tvNamaTersangka: TextView by lazy { itemView.findViewById(R.id.tv_nama_tersangka) }
        private val tvTglTerimaBerkas: TextView by lazy { itemView.findViewById(R.id.tv_tanggal_terima_berkas) }
        private val item: CardView by lazy { itemView.findViewById(R.id.cv_perkara_pidana_umum) }

        @SuppressLint("SetTextI18n")
        fun bindData(listItem: Model.DataResponse) {
            val date = LocalDate.parse(listItem.tgl_terimaBerkas)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            val formattedDate = date.format(formatter)

            tvNamaPenyidik.text = "Nama Penyidik : ${listItem.nama_penyidik}"
            tvNamaTersangka.text = "Nama Tersangka : ${listItem.nama_tersangka}"
            tvTglTerimaBerkas.text = "Tanggal terima berkas : $formattedDate"

            item.setOnClickListener {
                ContextCompat.startActivity(
                    itemView.context,
                    Intent(
                        itemView.context,
                        DetailInfoPerkaraPidanaUmumActivity::class.java
                    )
                        .putExtra("penyidik", listItem.nama_penyidik)
                        .putExtra("tersangka", listItem.nama_tersangka)
                        .putExtra("tanggal_p_16", listItem.p16)
                        .putExtra("tanggal_terima_berkas", listItem.tgl_terimaBerkas)
                        .putExtra("tanggal_spdp", listItem.tgl_spdp)
                        .putExtra("pasal", listItem.pasal)
                        .putExtra("tanggal_p_16_a", listItem.p16a)
                        .putExtra("keterangan", listItem.keterangan), null
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_perkara_pidana_umum, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}
