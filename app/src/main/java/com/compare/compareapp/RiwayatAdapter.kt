package com.compare.compareapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RiwayatAdapter(private val context: Context, private var ListPesanan: MutableList<dataRiwayat>) : RecyclerView.Adapter<RiwayatAdapter.MyViewHolder>(), Filterable {

    private var filteredListRiwayat = ListPesanan.toMutableList()

    class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val namaPembeli : TextView = itemView.findViewById(R.id.recNamaPembeli)
        val orderID : TextView = itemView.findViewById(R.id.recOrderID)
        val namaKereta : TextView = itemView.findViewById(R.id.recNamaKereta)
        val namaGerbong : TextView = itemView.findViewById(R.id.recNamaGerbong)
        val nomorKursi: TextView = itemView.findViewById(R.id.recNomorKursi)
        val status: TextView = itemView.findViewById(R.id.recstatusPesanan)
        val cardRiwayat : CardView = itemView.findViewById(R.id.recCardRiwayat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val CardListPesanan =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_riwayat, parent, false)
        return RiwayatAdapter.MyViewHolder(CardListPesanan)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.namaPembeli.text = filteredListRiwayat[position].namaUser
        holder.orderID.text = filteredListRiwayat[position].orderID
        holder.namaKereta.text = filteredListRiwayat[position].namaKereta
        val nomorgerbong = filteredListRiwayat[position].nomorGerbong
        val gerbong = filteredListRiwayat[position].Gerbong
        holder.namaGerbong.text = "$gerbong - $nomorgerbong"
        holder.nomorKursi.text = filteredListRiwayat[position].nomorKursi
        holder.status.text = filteredListRiwayat[position].status

        holder.cardRiwayat.setOnClickListener {
            val intent = Intent(context, detailPesanan::class.java)
            intent.putExtra("orderID", filteredListRiwayat[position].orderID)
            intent.putExtra("status", filteredListRiwayat[position].status)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = filteredListRiwayat.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = if (constraint.isNullOrBlank()) {
                    ListPesanan
                } else {
                    ListPesanan.filter { it.namaKereta?.contains(constraint, true)!! }
                }
                //ngecek hasil di log
                Log.d("MyAdapter","Filtered Result: $filteredResults")
                //mengembalikan nilai filter ke dalam values
                return FilterResults().apply { values = filteredResults }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredListRiwayat.clear()
                filteredListRiwayat.addAll(results?.values as MutableList<dataRiwayat>)
                notifyDataSetChanged()
            }
        }
    }
}