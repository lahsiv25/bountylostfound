package com.vishal.bountylostfound.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.dao.found
import com.vishal.bountylostfound.databinding.FragmentHomeBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import kotlin.math.roundToInt

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    lateinit var client: FusedLocationProviderClient
    var vishal: Task<Location>? = null
    private val database = Firebase.database(Constants.REFERENCE_URL)
    private val mAuth = FirebaseAuth.getInstance()
    private var userlist = arrayListOf<found>()
    private var userlist2 = arrayListOf<found>()
    private var favlistl = arrayListOf<String>()
    private var favlistf = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        while (view.findNavController().backQueue.size != 3){
            view.findNavController().navigateUp()
        }
        SharedViewmodel().currentLoc.value = false.toString()
        viewmodel.currentLoc.value = false.toString()
        val name:String? = mAuth.currentUser?.displayName
        if (name != null){
            binding.username.text = "Hello, $name"
        }else binding.username.text = "Hello, User"
        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        vishal = currentLocation()
        binding.AddNewCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFoundFragment()
            view.findNavController().navigate(action)
        }
        binding.vastzone.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("uploading")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val filter = "allData"
            getFullUserData(vishal!!.result.latitude!!,vishal!!.result.longitude!!,"allData","allUser",filter)
            viewmodel.mlu1.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    if(progressDialog.isShowing){
                        progressDialog.dismiss()
                    }
                    val action = HomeFragmentDirections.actionHomeFragmentToViewPagerFragment()
                    viewmodel.mlu1.value = false
                    view.findNavController().navigate(action)
                }
            })
        }
        binding.favouriteCard.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("uploading")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val user = FirebaseAuth.getInstance().currentUser!!.uid
            val filter = "favourite"
            getFullUserData(vishal!!.result.latitude!!,vishal!!.result.longitude!!,"Favourites",user,filter)
            viewmodel.mlu1.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    if(progressDialog.isShowing){
                        progressDialog.dismiss()
                    }
                    val action = HomeFragmentDirections.actionHomeFragmentToViewPagerFragment()
                    viewmodel.mlu1.value = false
                    view.findNavController().navigate(action)
                }
            })
        }
        binding.bountyCard.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("uploading")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val filter = "bounty"
            getFullUserData(vishal!!.result.latitude!!,vishal!!.result.longitude!!,"allData","allUser",filter)
            viewmodel.mlu1.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    if(progressDialog.isShowing){
                        progressDialog.dismiss()
                    }
                    val action = HomeFragmentDirections.actionHomeFragmentToViewPagerFragment()
                    viewmodel.mlu1.value = false
                    view.findNavController().navigate(action)
                }
            })
        }

    }
    fun currentLocation():Task<Location>{
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
    fun getFullUserData(lat:Double,lon:Double,node:String,user:String,filter:String){
        userlist = arrayListOf()
        userlist2 = arrayListOf()
        favlistf = arrayListOf()
        favlistl = arrayListOf()


        database.getReference("Favourites").child(mAuth.currentUser!!.uid).child("foundEntry")
            .get().addOnSuccessListener {
                for (i in it.children) {
                    val u = i.getValue(found::class.java)
                    if (u != null) {
                        favlistf.add(u.orderId!!)
                    }
                }
                database.getReference(node).child(user).child("foundEntry")
                    .get().addOnSuccessListener {
                        for (userSnapshot in it.children) {
                            val user = userSnapshot.getValue(found::class.java)
                            val distanceActual =
                                viewmodel.distanceInKm(lat, lon, user?.latitude!!, user.longitude!!)
                                    .roundToInt()
                            user.distance = distanceActual
                            if (favlistf.contains(user.orderId)) {
                                user.favourite = true
                            }
                            if (filter == "bounty"){
                                if (user.bountyOrNot!!){userlist.add(user!!)}
                            }else{userlist.add(user!!)}
                        }
                        viewmodel.userList.value = userlist
                    }
            }
        database.getReference("Favourites").child(mAuth.currentUser!!.uid).child("lostEntry")
            .get().addOnSuccessListener {
                for (i in it.children){
                    var uu = i.getValue(found::class.java)
                    if (uu != null) {
                        favlistl.add(uu.orderId!!)
                    }
                }
                database.getReference(node).child(user).child("lostEntry")
                    .get().addOnSuccessListener {
                        for (userSnapshot in it.children){
                            val user = userSnapshot.getValue(found::class.java)
                            val distanceActual = viewmodel.distanceInKm(lat,lon,user?.latitude!!,user.longitude!!).roundToInt()
                            user.distance = distanceActual
                            if (favlistl.contains(user.orderId)){
                                user.favourite = true
                            }
                            if (filter == "bounty"){
                                if (user.bountyOrNot!!){userlist2.add(user!!)}
                            }else{userlist2.add(user!!)}
                        }
                        viewmodel.userList2.value = userlist2
                        viewmodel.mlu1.value = true
                    }
            }
    }
}