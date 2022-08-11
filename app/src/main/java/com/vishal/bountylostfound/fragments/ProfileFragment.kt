package com.vishal.bountylostfound.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vishal.bountylostfound.activity.MainActivity
import com.vishal.bountylostfound.databinding.FragmentProfileBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import kotlinx.coroutines.launch
import java.util.*


class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    private val user:  FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var client:FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding
        val view = binding.root
        if (user != null){
            Glide.with(this)
                .load(user.photoUrl).into(binding.imgprofile)
            binding.apply {
                textViewUsername.text = user.displayName
                textud1.text = user.uid
                textud2.text = user.email
                if (user.phoneNumber?.trim()  != ""){textud3.text = user.phoneNumber}
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            location()
        }
        binding.privacypolicy.setOnClickListener {
            //Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
            val urlString = "https://bountyappprivacypolicy.blogspot.com/2022/08/privacy-policy-for-bounty.html"
            val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            intent1.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome")
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.setPackage("com.android.chrome")
            startActivity(intent1)
        }
        binding.termsandconditions.setOnClickListener {
            //Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
            val urlString = "https://bountyappprivacypolicy.blogspot.com/2022/08/terms-conditions-for-bounty.html"
            val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            intent1.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome")
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.setPackage("com.android.chrome")
            startActivity(intent1)
        }
        binding.signout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
     fun location(){
        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationLatLong = currentLocation()
        locationLatLong.addOnSuccessListener {
            val gc = Geocoder(requireContext(), Locale.getDefault())
            val addresses:MutableList<Address> = gc.getFromLocation(it.latitude,it.longitude,1)
            if(addresses.size != 0){
                val finalLocation = addresses[0].getAddressLine(0)
                binding.textRecentloc.text = finalLocation
            }
        }
    }
    fun currentLocation(): Task<Location> {
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
}