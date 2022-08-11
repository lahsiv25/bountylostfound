package com.vishal.bountylostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vishal.bountylostfound.R

class ImageAdapter(private val uris: MutableList<String>):RecyclerView.Adapter<ImageAdapter.ViewHolder>(){
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val a = itemView.findViewById<ImageView>(R.id.imageitem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = uris[position]
        Picasso
          .get() // give it the context
        .load(uri.trim()) // load the image
        .into(holder.a)

    }

    override fun getItemCount(): Int {
        return  uris.size
    }

}