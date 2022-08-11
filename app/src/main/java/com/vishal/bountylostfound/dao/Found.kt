package com.vishal.bountylostfound.dao

import android.os.Parcel
import android.os.Parcelable

@found.Parcelize
data class found(val date:String? = null, var distance:Int? = null
                 , val orderId:String? = null, val lostOrFound:String? = null
                 , val name:String? = null, val description:String? = null
                 , val phone:String? = null, val location:String? = null
                 , val latitude:Double? = null, val longitude:Double? = null
                 , val locationCode:Int? = null, val photoURI: MutableList<String>? = null
                 , val bountyOrNot:Boolean? = null, val bountyAmount:Int? = null
                 ,var favourite:Boolean? = null,val userName:String? = null
                 ,val countryCode:String? = null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("photoURI"),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    annotation class Parcelize

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeValue(distance)
        parcel.writeString(orderId)
        parcel.writeString(lostOrFound)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(phone)
        parcel.writeString(location)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeValue(locationCode)
        parcel.writeValue(bountyOrNot)
        parcel.writeValue(bountyAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<found> {
        override fun createFromParcel(parcel: Parcel): found {
            return found(parcel)
        }

        override fun newArray(size: Int): Array<found?> {
            return arrayOfNulls(size)
        }
    }
}
