package com.vishal.bountylostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vishal.bountylostfound.fragments.HistoryFragmentDirections
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.dao.found

class MyHistoryAdapter2(
    val text:ArrayList<found>
): RecyclerView.Adapter<MyHistoryAdapter2.ViewPagerViewHolder>() {
    val b = ArrayList<found>()

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val a = itemView.findViewById<TextView>(R.id.nameText)
        val b = itemView.findViewById<TextView>(R.id.bountyText)
        val c = itemView.findViewById<TextView>(R.id.locationText)
        val d = itemView.findViewById<ImageView>(R.id.ImageViewB)
        val e = itemView.findViewById<TextView>(R.id.distanceText)
        val f = itemView.findViewById<TextView>(R.id.dateText)
        val g = itemView.findViewById<ImageView>(R.id.iconicon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val context = holder.itemView.context
        val currentitem = text[position]
        holder.a.text = currentitem.name
        holder.b.text = currentitem.bountyAmount.toString()
        holder.c.text = currentitem.location
        holder.e.text = currentitem.lostOrFound
        holder.f.text = currentitem.date
        holder.g.setImageResource(R.drawable.descriptionsmall)
        var uuu = "hello"
        if (currentitem.photoURI != null) {
            uuu = currentitem.photoURI[0]
            Glide.with(context)
                .load(uuu).into(holder.d)
        } else{
            holder.d.setImageResource(R.drawable.fiiiii)
        }
        holder.itemView.setOnClickListener {
            val action =
                HistoryFragmentDirections.actionHistoryFragmentToItemFoundFragment2(currentitem)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return text.size
    }
}