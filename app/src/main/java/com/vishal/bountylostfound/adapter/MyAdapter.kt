package com.vishal.bountylostfound.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishal.bountylostfound.fragments.ViewPagerFragmentDirections
import com.squareup.picasso.Picasso
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.dao.found

class MyAdapter( var userlist:ArrayList<found>):ListAdapter<found, MyAdapter.ViewHolder>(
    DiffCallback
) {
    inner class ViewHolder(val binding: View):RecyclerView.ViewHolder(binding){
        val a = itemView.findViewById<TextView>(R.id.nameText)
        val b = itemView.findViewById<TextView>(R.id.bountyText)
        val c = itemView.findViewById<TextView>(R.id.locationText)
        val d = itemView.findViewById<ImageView>(R.id.ImageViewB)
        val e = itemView.findViewById<TextView>(R.id.distanceText)
        val f = itemView.findViewById<TextView>(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = userlist[position]
        holder.a.text = currentitem.name
        holder.b.text = currentitem.bountyAmount.toString()
        holder.c.text = currentitem.location

        if (currentitem.distance == 0){
            holder.e.text = "Less than one Km"
        }else holder.e.text = "${currentitem.distance.toString()} km"
        holder.f.text = currentitem.date
        if (currentitem.photoURI == null){
            holder.d.setImageResource(R.drawable.fiiiii)
        }else {
            Picasso
                .get() // give it the context
                .load(currentitem.photoURI.get(0)) // load the image
                .into(holder.d)
        }
        holder.itemView.setOnClickListener {
            val db: found = currentitem
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToItemFoundFragment(db)
            it.findNavController().navigate(action)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<found>() {
            override fun areItemsTheSame(oldItem: found,
                                         newItem: found
            ): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(oldItem: found,
                                            newItem: found
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }
        }
    }
}