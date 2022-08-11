package com.vishal.bountylostfound.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.dao.FinalData
import com.vishal.bountylostfound.dao.found
import kotlin.math.roundToInt

open class SharedViewmodel:ViewModel() {

    val database = Firebase.database(Constants.REFERENCE_URL)
    val userList  = MutableLiveData<ArrayList<found>>()
    val userList2  = MutableLiveData<ArrayList<found>>()
    var currentLoc = MutableLiveData<String>()
    var mlu1 = MutableLiveData(false)
    var condition = MutableLiveData(false)
    val amt = MutableLiveData("100")
    val amtedit = MutableLiveData("100")



    fun AddFoundDatabase(Date: String, Distance: Int, OrderId: String, LostOrFound: String,
        Name: String, Description: String, Phone: String, Location: String, Latitude: Double,
        Longitude: Double, LocationCode: Int, PhotoURI: MutableList<String?>, BountyOrNot: Boolean,
        BountyAmount: Int, Favourite: Boolean, UserName:String, CountryCode:String):Task<Void>{
        val datas = FinalData(Date,Distance,
            OrderId,LostOrFound, Name,
            Description, Phone, Location, Latitude,
            Longitude, LocationCode, PhotoURI, BountyOrNot, BountyAmount,Favourite,UserName,CountryCode)

        return database.getReference("allData").child("allUser").child(LostOrFound).child(OrderId).setValue(datas)
    }

    fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
    fun assignLocatioCode(latitude:Double,longitude:Double):Int{
        val latitude1 = latitude.roundToInt()
        val longitude1 = longitude.roundToInt()
        val a:Int = (latitude1* -1) + 90
        val b = longitude1+ 181
        val code = (a*361)+ b
        return code
    }


}

