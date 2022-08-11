package com.vishal.bountylostfound.dao

data class FinalData(val Date:String, val Distance:Int,
                     val OrderId:String, val LostOrFound:String
                     , val Name:String, val Description:String
                     , val Phone:String, val Location:String
                     , val latitude:Double, val Longitude:Double
                     , val LocationCode:Int, val PhotoURI: MutableList<String?>
                     , val BountyOrNot:Boolean, val BountyAmount:Int
                     , val Favourite:Boolean, val UserName:String, val CountryCode:String )
