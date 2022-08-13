package com.vishal.bountylostfound.addDataFragment

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Browser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.adapter.ImageAdapter
import com.vishal.bountylostfound.dao.FinalData
import com.vishal.bountylostfound.dao.found
import com.vishal.bountylostfound.databinding.FragmentEditBinding
import com.vishal.bountylostfound.fragments.FragmentSearchpopup
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.vishal.bountylostfound.fragments.ItemFoundFragmentArgs


class EditFragment : Fragment(),PaymentResultListener {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    var imageUri: MutableList<String?> = mutableListOf()
    var imageuris:MutableList<Uri?> = mutableListOf()
    var imageUrisString:MutableList<String> = mutableListOf()
    private val mAuth = FirebaseAuth.getInstance()
    val database = Firebase.database(Constants.REFERENCE_URL)
    val args: ItemFoundFragmentArgs by navArgs()
    lateinit var data: found
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null




    companion object{
        val REQUEST_CODE = 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        binding
        val view = binding.root
        data = args.data
        return view
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomsheet:View = binding.includedBottomSheet.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        (bottomSheetBehavior as BottomSheetBehavior<*>).state = BottomSheetBehavior.STATE_HIDDEN
        viewmodel.amtedit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.includedBottomSheet.amount.text = it.toString()
        })
        Checkout.preload(requireContext())
        var ll = data.latitude
        var lll = data.longitude
        val supportFragmentManager = childFragmentManager
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY1",
            this
        ) { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY1") {
                val date = bundle.getStringArrayList("SELECTED_DATE")
                binding.LocationEditText1.setText(date?.get(0))
                ll = date?.get(1)?.toDouble()!!
                lll = date.get(2)?.toDouble()!!
            }
        }
        if (data.lostOrFound == "foundEntry"){
            binding.FoundOrLostDropDown.hint = "Add a found product"
        }else binding.FoundOrLostDropDown.hint = "Add a lost product"
        binding.FoundOrLostDropDown1.isFocusable = false
        //binding.FoundOrLostDropDown.editText!!.setSelection(1)
        binding.NameEditText1.setText(data.name)
        binding.DescriptionEditText1.setText(data.description)
        binding.PhoneEdittext1.setText(data.phone)
        binding.LocationEditText1.setText(data.location)
        binding.Countrypicker.setCountryForNameCode(data.countryCode)
        binding.FoundOrLostDropDown1.setOnClickListener {
            // Toast.makeText(requireContext(), "Lost or found can't be changed", Toast.LENGTH_SHORT).show()
        }

        binding.AddImageButton.setOnClickListener {
            SelectImage()
        }
        binding.LocationEditText1.setOnFocusChangeListener { view, b ->
            if (view.isFocused == false){
                val datePickerFragment = FragmentSearchpopup()
                datePickerFragment.show(childFragmentManager, "DatePickerFragment")
            }
            binding.LocationEditText.clearFocus()
        }

        binding.includedBottomSheet.increase.setOnClickListener {
            val newamount = (viewmodel.amtedit.value!!.toInt() + 100).toString()
            if (newamount.toInt() >= 100 && newamount.toInt() <= 99999) {
                viewmodel.amtedit.value = newamount
            }

        }
        binding.includedBottomSheet.decrease.setOnClickListener {
            val newamount = (viewmodel.amtedit.value!!.toInt() - 100).toString()
            if (newamount.toInt() >= 100 && newamount.toInt() <= 99999) {
                viewmodel.amtedit.value = newamount
            }
        }
        binding.includedBottomSheet.proceedpayment.setOnClickListener {
            if (binding.includedBottomSheet.tccheck.isChecked){
                savePayments(binding.includedBottomSheet.amount.text.toString().toInt())
            }else{
                //Toast.makeText(requireContext(),   "accept terms and conditions", Toast.LENGTH_SHORT).show()
            }

        }
        binding.includedBottomSheet.tc.setOnClickListener {
            val urlString = "https://bountyappprivacypolicy.blogspot.com/2022/08/terms-conditions-for-bounty.html"
            val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            intent1.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome")
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.setPackage("com.android.chrome")
            startActivity(intent1)
        }

        binding.SubmitButton.setOnClickListener {
            var puriSize = 0
            if (data.photoURI != null){
                puriSize = data.photoURI!!.size
            }
            var LostOrFound:String? = null
            var Name:String? = null
            var Description:String? = null
            var Phone:String? = null
            var CountryCode:String? = null
            var Location:String? = null
            var PhotoURI:String? = null
            val OrderId = data.orderId!!
            val UserName = data.userName!!

            val Date = data.date
            val Distance = 0
            LostOrFound = data.lostOrFound
            val NameEditText1 = binding.NameEditText1.text.toString()
            if(NameEditText1.isEmpty()){
                binding.NameEditText1.error = "Enter a name"
                return@setOnClickListener
            }else{
                Name = NameEditText1
            }
            val DescriptionEditText1 = binding.DescriptionEditText1.text.toString()
            if(DescriptionEditText1.isEmpty()){
                binding.DescriptionEditText1.error = "Give a Description"
                return@setOnClickListener
            }else{
                Description = DescriptionEditText1
            }
            val PhoneEdittext1 = binding.PhoneEdittext1.text.toString()
            if(PhoneEdittext1.isEmpty()){
                binding.PhoneEdittext1.error = "Enter a valid phone number"
                return@setOnClickListener
            }else{
                CountryCode = binding.Countrypicker.selectedCountryNameCode
                Phone = "$PhoneEdittext1"
            }
            val LocationEditText1 = binding.LocationEditText1.text.toString()
            if(LocationEditText1.isEmpty()){
                binding.LocationEditText1.error = "Choose a location"
                return@setOnClickListener
            }else{
                Location = LocationEditText1
            }
            val Latitude = ll
            val Longitude = lll
            val LocationCode = viewmodel.assignLocatioCode(Latitude!!,Longitude!!)
            val BountyOrNot = data.bountyOrNot
            val BountyAmount = data.bountyAmount
            val Favourite = data.favourite
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("uploading")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if (imageUrisString.size != 0){
                for (i in imageUrisString){
                    val ii = Uri.parse(i)
                    val storageReference = FirebaseStorage.getInstance().getReference("FoundImages/${OrderId}${puriSize+imageUrisString.indexOf(i)+1}")
                    storageReference.putFile(ii!!).addOnCompleteListener {
                        if (it.isSuccessful){
                            it.result.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val imageuri2 = it.result.toString()
                                    imageUri.add(imageuri2)
                                    if (imageUrisString.indexOf(i)+1 == imageUrisString.count() ){
                                        if (data.photoURI != null){
                                            for (i in data.photoURI!!){imageUri.add(i)}
                                        }
                                        viewmodel.AddFoundDatabase(
                                            Date!!, Distance, OrderId!!, LostOrFound!!, Name,
                                            Description, Phone, Location, Latitude, Longitude,
                                            LocationCode, imageUri, BountyOrNot!!, BountyAmount!!,
                                            Favourite!!, UserName, CountryCode).
                                        addOnCompleteListener {
                                            if (it.isSuccessful){
                                                database.getReference("History")
                                                    .child(mAuth.currentUser!!.uid).child(OrderId).setValue(
                                                        FinalData(Date,Distance,OrderId!!
                                                            ,LostOrFound!!,Name,Description,Phone,Location,Latitude,Longitude
                                                            ,LocationCode,imageUri, BountyOrNot,BountyAmount,Favourite
                                                            ,UserName,CountryCode)
                                                    ).addOnSuccessListener {
                                                        //Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                                                        if(progressDialog.isShowing){
                                                            progressDialog.dismiss()}
                                                        if (LostOrFound == "lostEntry"){
                                                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                                                .setTitle("Add Bounty?")
                                                                .setCancelable(false)
                                                                .setMessage("Do you like to add bounty for your product")
                                                                .setNegativeButton(
                                                                    ("No"),
                                                                    DialogInterface.OnClickListener { arg0, arg1 ->
                                                                        val navController = Navigation.findNavController(requireView())
                                                                        val action = EditFragmentDirections.actionEditFragmentToHistoryFragment()
                                                                        navController.navigate(action)
                                                                    })
                                                                .setPositiveButton(
                                                                    ("yes"),
                                                                    DialogInterface.OnClickListener {
                                                                            arg0, arg1 ->
                                                                        binding.llMask.isVisible = true
                                                                        (bottomSheetBehavior as BottomSheetBehavior<*>).isHideable = false
                                                                        (bottomSheetBehavior as BottomSheetBehavior<*>).state = BottomSheetBehavior.STATE_EXPANDED
                                                                    })
                                                                .create().show()
                                                        }else if (LostOrFound == "foundEntry"){
                                                            val navController = Navigation.findNavController(requireView())
                                                            val action = EditFragmentDirections.actionEditFragmentToHistoryFragment()
                                                            navController.navigate(action)
                                                        }
                                                    }
                                            }else if (it.isCanceled){
                                                //Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                if (data.photoURI != null){
                    for (i in data.photoURI!!){imageUri.add(i)}
                }
                viewmodel.AddFoundDatabase(
                    Date!!, Distance, OrderId!!, LostOrFound!!, Name, Description, Phone,
                    Location, Latitude, Longitude, LocationCode, imageUri, BountyOrNot!!,
                    BountyAmount!!, Favourite!!, UserName, CountryCode).addOnCompleteListener {
                    if (it.isSuccessful){
                        database.getReference("History")
                            .child(mAuth.currentUser!!.uid).child(OrderId).setValue(
                                FinalData(Date,Distance,OrderId!!
                                    ,LostOrFound!!,Name,Description,Phone,Location,Latitude,Longitude
                                    ,LocationCode,imageUri,BountyOrNot,BountyAmount,Favourite
                                    ,UserName,CountryCode)
                            ).addOnSuccessListener {
                                //Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                                if(progressDialog.isShowing){
                                    progressDialog.dismiss()}
                                if (LostOrFound == "lostEntry"){
                                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        .setTitle("Add Bounty?")
                                        .setCancelable(false)
                                        .setMessage("Do you like to add bounty for your product")
                                        .setNegativeButton(
                                            ("No"),
                                            DialogInterface.OnClickListener { arg0, arg1 ->
                                                val navController = Navigation.findNavController(requireView())
                                                val action = EditFragmentDirections.actionEditFragmentToHistoryFragment()
                                                navController.navigate(action)
                                            })
                                        .setPositiveButton(
                                            ("yes"),
                                            DialogInterface.OnClickListener {
                                                    arg0, arg1 ->
                                                binding.llMask.isVisible = true
                                                (bottomSheetBehavior as BottomSheetBehavior<*>).isHideable = false
                                                (bottomSheetBehavior as BottomSheetBehavior<*>).state = BottomSheetBehavior.STATE_EXPANDED
                                            })
                                        .create().show()
                                }else if (LostOrFound == "foundEntry"){
                                    val navController = Navigation.findNavController(requireView())
                                    val action = EditFragmentDirections.actionEditFragmentToHistoryFragment()
                                    navController.navigate(action)
                                }
                            }
                    }else if (it.isCanceled){
                        //Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    fun SelectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){
            var count:Int? = data?.clipData?.itemCount
            if (count == null){
                var uri = data?.data!!.toString()
                imageUrisString.add(uri!!)
                val imgadapter = ImageAdapter(imageUrisString)
                binding.PhotoView.adapter = imgadapter
            }else{
                for (i in 0..count!!-1){
                    var uri = data?.clipData?.getItemAt(i)?.uri.toString()
                    imageUrisString.add(uri!!)
                }
                val imgadapter = ImageAdapter(imageUrisString)
                binding.PhotoView.adapter = imgadapter
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun date():String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        return formatted.toString()
    }


    private fun savePayments(amt:Int){
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_D7778qmXL8moMc")
        try {
            val options = JSONObject()
            options.put("name","pay Bounty")
            options.put("description","you've added a bounty of $amt inr")
            options.put("theme.color","#ff2c5a")
            options.put("currency","INR")
            options.put("amount",amt*100)

            val retryObject = JSONObject()

            retryObject.put("enabled","true")
            retryObject.put("max_count",4)
            retryObject.put("retry",retryObject)

            checkout.open(requireActivity(),options)
        }catch (e:Exception){
            //Toast.makeText(requireContext(), "Error in payment {e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        //Toast.makeText(requireContext(), "p0", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        //Toast.makeText(requireContext(), "p1", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        if ((bottomSheetBehavior as BottomSheetBehavior<*>).state != BottomSheetBehavior.STATE_HIDDEN ){
            binding.llMask.isVisible = true
        }
    }

}