package com.vishal.bountylostfound.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.databinding.FragmentSearchPopupBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import java.util.*

class FragmentSearchpopup: DialogFragment() {

    private var _binding: FragmentSearchPopupBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    var items = mutableListOf<ArrayList<String>?>()
    lateinit var client:FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchPopupBinding.inflate(inflater, container, false)
        binding
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewmodel().currentLoc.value = false.toString()
        binding.searchbar.isFocusedByDefault = true
        val adapter1 = SearchLocationAdapter(items)
        binding.searchresults.adapter = adapter1

        binding.searchbar.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.searchbar.clearFocus()
                val LocationEditText1 = p0
                if (LocationEditText1!!.length != 0) {
                    val gc = Geocoder(requireContext(), Locale.getDefault())
                    val addresses:MutableList<Address> = gc.getFromLocationName(LocationEditText1,5)
                    items.clear()
                    for (i in addresses){
                        var data:String? = i.getAddressLine(0)
                        var pin:String? = i.postalCode
                        var locality:String? = i.locality
                        if (data == null){
                            data = " "
                        }
                        if (pin == null){
                            pin = " "
                        }
                        if (locality == null){
                            locality = " "
                        }
                        var add = "$data $locality $pin"
                        var lat = i.latitude
                        var lon = i.longitude
                        items.add(arrayListOf(add,lat.toString(),lon.toString()))
                    }
                    adapter1.notifyDataSetChanged()
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val LocationEditText1 = p0
                if (LocationEditText1!!.length != 0) {
                    val gc = Geocoder(requireContext(), Locale.getDefault())
                    val addresses:MutableList<Address> = gc.getFromLocationName(LocationEditText1,5)
                    items.clear()
                    for (i in addresses){
                        var data:String? = i.getAddressLine(0)
                        var pin:String? = i.postalCode
                        var locality:String? = i.locality
                        if (data == null){
                            data = " "
                        }
                        if (pin == null){
                            pin = " "
                        }
                        if (locality == null){
                            locality = " "
                        }
                        var add = "$data $locality $pin"
                        var lat = i.latitude
                        var lon = i.longitude
                        items.add(arrayListOf(add,lat.toString(),lon.toString()))
                    }
                    adapter1.notifyDataSetChanged()
                }
                return true
            }
        })
        binding.findcurrentlocation.setOnClickListener {
            client = LocationServices.getFusedLocationProviderClient(requireActivity())
            val vishal = currentLocation(context)
            vishal.addOnCompleteListener {
                if (it.isSuccessful){
                    val gc = Geocoder(requireContext(),Locale.getDefault())
                    val addresses:MutableList<Address> = gc.getFromLocation(it.result.latitude,it.result.longitude,1)
                    if(addresses.size != 0){
                        val address = addresses.get(0)
                        val selectedDateBundle = Bundle()
                        var cr = arrayListOf(
                            address.getAddressLine(0)
                            ,address.latitude.toString()
                            ,address.longitude.toString())
                        selectedDateBundle.putStringArrayList("SELECTED_DATE", cr)

                        setFragmentResult("REQUEST_KEY1", selectedDateBundle)
                        dismiss()
                    }else{ //Toast.makeText(requireContext(), "Invalid address", Toast.LENGTH_SHORT).show()
                     }
                }
            }
        }

    }
    fun currentLocation(context: Context?): Task<Location> {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ){
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    //Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
        viewmodel.condition.value = true
        return client.lastLocation
    }
    inner class SearchLocationAdapter(val text:MutableList<ArrayList<String>?>)
        : RecyclerView.Adapter<SearchLocationAdapter.ViewPagerViewHolder>() {
        inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val a = itemView.findViewById<TextView>(R.id.searchtextlocation)
        }


        override fun getItemCount(): Int {
            return  text.size
        }

        override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
            val currentitem = text[position]
            holder.a.text = currentitem?.get(0)
            holder.itemView.setOnClickListener {
                val selectedDateBundle = Bundle()
                selectedDateBundle.putStringArrayList("SELECTED_DATE", currentitem)

                setFragmentResult("REQUEST_KEY1", selectedDateBundle)
                dismiss()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_list_item,parent,false)
            return ViewPagerViewHolder(view)
        }
    }
}