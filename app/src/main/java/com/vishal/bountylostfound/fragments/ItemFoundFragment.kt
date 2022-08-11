package com.vishal.bountylostfound.fragments

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.adapter.ImageAdapter
import com.vishal.bountylostfound.dao.found
import com.vishal.bountylostfound.databinding.FragmentItemFoundBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ItemFoundFragment:Fragment() {

    private var _binding: FragmentItemFoundBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    val args: ItemFoundFragmentArgs by navArgs()
    val database = Firebase.database(Constants.REFERENCE_URL)
    private val mAuth = FirebaseAuth.getInstance()
    lateinit var data: found
    var undo = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemFoundBinding.inflate(inflater, container, false)
        binding
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = args.data
        binding.name.setText(data.name)
        binding.postedby.setText(data.userName)
        binding.description.setText(data.description)
        binding.lostorfound.setText(data.lostOrFound)
        binding.address.setText(data.location)
        binding.contactNumber.setText("+${data.countryCode} ${data.phone}")
        binding.postedon.setText(data.date)
        binding.bountyamount.setText(data.bountyAmount.toString())
        binding.entrynumber.setText(data.orderId)
        binding.cbHeart.isChecked = data.favourite!!
        if (data.lostOrFound == "lostEntry"){
            binding.foundButton.isVisible = true
        }
        val uris = mutableListOf<Uri?>()
        if (data.photoURI == null){
            binding.imageitemindividual.isVisible = true
        }else{for (i in data.photoURI!!){
            val uri = Uri.parse(i)
            uris.add(uri)
            val imgadapter2 = ImageAdapter(data.photoURI!!)
            binding.vpitemimage.adapter = imgadapter2
        }}
        callPermission()
        binding.cbHeart.setOnCheckedChangeListener { checkBox, isChecked ->

            if (isChecked) {
                data.favourite = true
                database.getReference("Favourites").child(mAuth.currentUser!!.uid)
                    .child(data.lostOrFound!!).child(data.orderId!!).setValue(data)
                //showToast("Item added to Wishlist")
            } else {
                database.getReference("Favourites").child(mAuth.currentUser!!.uid)
                    .child(data.lostOrFound!!).child(data.orderId!!).removeValue()
                data.favourite = false
                //showToast("Item removed from Wishlist")
            }
        }
        binding.foundButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Notify the User")
                .setCancelable(false)
                .setMessage("By clicking the notify button below you have a chance to claim 80% of the Bounty fixed by the other user. We will confirm if you've got the exact item with the other user and once the user got their item back we will contact you for the Bounty reward")
                .setNegativeButton(
                    ("No"),
                    DialogInterface.OnClickListener { arg0, arg1 ->
                        //showToast("cancelled")
                    })
                .setNeutralButton(
                    ("Notify"),
                    DialogInterface.OnClickListener {
                            arg0, arg1 ->
                        val time = date()
                        undo = "${mAuth.currentUser!!.uid}${time}"
                        database.getReference("Notification")
                            .child("${mAuth.currentUser!!.uid}${time}").setValue(
                                "Found ${data.orderId}"
                            ).addOnSuccessListener {
                                val snackbar = Snackbar.make(view,"",Snackbar.LENGTH_LONG)
                                val customSnackbar:View = layoutInflater.inflate(R.layout.snackbar,null)
                                snackbar.view.setBackgroundColor(Color.TRANSPARENT)
                                val snackBarView = snackbar.view as Snackbar.SnackbarLayout
                                customSnackbar.findViewById<View>(R.id.tv_undo).setOnClickListener {
                                    database.getReference("Notification")
                                        .child(undo).removeValue().addOnSuccessListener {
                                            snackbar.dismiss()
                                        }
                                }
                                snackBarView.addView(customSnackbar,0)
                                snackbar.show()
                            }.addOnFailureListener {
                                //showToast("Failed to notify try again")
                            }
                    })
                .create().show()
        }
        binding.call.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                        callPermission()
            }else{
                    if (data.phone!!.isNotEmpty()){
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:+${data.countryCode} ${data.phone}")
                        startActivity(callIntent)
                    }else{
                        //showToast("Invalid number")
                    }
            }

        }
        binding.locate.setOnClickListener {
            val uri = Uri.parse("geo:${data.latitude},${data.longitude}")
            val localIntent = Intent(Intent.ACTION_VIEW,uri)
            localIntent.setPackage("com.google.android.apps.maps")
            startActivity(localIntent)
        }
    }

    private fun callPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.CALL_PHONE,
            ), 101
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showToast(str: String) {
        //Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun date():String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmSS")
        val formatted = current.format(formatter)
        return formatted.toString()
    }
}