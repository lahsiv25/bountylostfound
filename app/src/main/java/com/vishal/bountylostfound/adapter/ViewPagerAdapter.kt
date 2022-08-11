package com.vishal.bountylostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.dao.found

class ViewPagerAdapter(val text: List<MutableLiveData<ArrayList<found>>>):
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    val b = ArrayList<found>()
    inner class ViewPagerViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val recy = itemView.findViewById<RecyclerView>(R.id.recyclerView)
        val searchbar = itemView.findViewById<SearchView>(R.id.searchbar)
        val emptycard = itemView.findViewById<CardView>(R.id.emptycard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager,parent,false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curtext = text[position]
        val adapter1 = curtext.value?.let { MyAdapter(it) }
        if (adapter1 != null && curtext.value!!.size != 0) {
            adapter1.submitList(curtext.value)
            holder.recy.adapter = adapter1
            holder.searchbar.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    holder.searchbar.clearFocus()
                    val a: MutableLiveData<ArrayList<found>> = MutableLiveData(arrayListOf())
                    for (i in curtext.value!!){
                        if ((i.toString()).contains(p0!!.trim(), ignoreCase = true)){
                            a.value!!.add(i)
                        }
                    }
                    adapter1!!.submitList(a.value)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    val a: MutableLiveData<ArrayList<found>> = MutableLiveData(arrayListOf())
                    a.value!!.clear()
                    for (i in curtext.value!!){
                        if ((i.toString()).contains(p0!!.trim(), ignoreCase = true)){
                            a.value!!.add(i)
                        }
                    }
                    adapter1!!.submitList(a.value)
                    adapter1.submitList(a.value)
                    return false
                }
            })
        }else if (curtext.value!!.size == 0){
            holder.emptycard.isVisible = true
        }
    }

    override fun getItemCount(): Int {
        return text.size
    }
}