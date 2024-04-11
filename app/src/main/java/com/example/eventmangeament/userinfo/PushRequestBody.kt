package com.example.eventmangeament.userinfo


import com.google.gson.annotations.SerializedName

class PushRequestBody : ArrayList<PushRequestBody.PushRequestBodyItem>(){
    data class PushRequestBodyItem(
        @SerializedName("isDinner")
        val isDinner: Int,
        @SerializedName("isLunch")
        val isLunch: Int,
        @SerializedName("rfidNo")
        val rfidNo: String
    )
}

data class RfidRequestBody(
@SerializedName("DinnerAccess")
var dinnerAccess: Int,
@SerializedName("EventAccess")
val eventAccess: Int,
@SerializedName("FullAccess")
var fullAccess: Int,
@SerializedName("LunchAccess")
var lunchAccess: Int,
@SerializedName("PowerMeetingAccess")
var powerMeetingAccess: Int,
@SerializedName("RfidNumber")
val rfidNumber: String)

//data class RfidRequestBody(
//    val rfidNo: String,
//    val isfood: Int,
//    val ispowertimezone: Int,
//    val  isexhibition:Int
//)