package com.compare.compareapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val context: Context, private var MenuList: MutableList<Menu>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val judulMenu : TextView = itemView.findViewById(R.id.recTittle)
        val HargaMenu : TextView = itemView.findViewById(R.id.recPrice)
        val fotoMenu : ImageView = itemView.findViewById(R.id.recImage)
        val card : CardView = itemView.findViewById(R.id.recCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val menuView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(menuView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       Glide.with(context).load(MenuList[position].Foto).into(holder.fotoMenu)
        holder.judulMenu.text = MenuList[position].namaMenu
        holder.HargaMenu.text = MenuList[position].Harga

        holder.card.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("Image", MenuList[holder.adapterPosition].Foto)
            intent.putExtra("namaMenu", MenuList[holder.adapterPosition].namaMenu)
            intent.putExtra("Harga", MenuList[holder.adapterPosition].Harga)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return MenuList.size
    }

//    fun searchDataList(searchList :List<Menu>){
//        MenuList = searchList as MutableList<Menu>
//        notifyDataSetChanged()
//    }
}