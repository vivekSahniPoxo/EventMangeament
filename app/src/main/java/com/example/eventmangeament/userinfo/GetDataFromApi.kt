package com.example.eventmangeament.userinfo


import com.google.gson.annotations.SerializedName



class GetDataFromApi : ArrayList<GetDataFromApi.GetDataFromApiItem>(){
    data class GetDataFromApiItem(
        @SerializedName("category")
        val category: String,
        @SerializedName("chapterName")
        val chapterName: String,
        @SerializedName("companyName")
        val companyName: String,
        @SerializedName("dinnerAccess")
        val dinnerAccess: Int,
        @SerializedName("emailAddress")
        val emailAddress: String,
        @SerializedName("eventAccess")
        val eventAccess: Int,
        @SerializedName("fullAccess")
        val fullAccess: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("lunchAccess")
        val lunchAccess: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("phoneNumber")
        val phoneNumber: String,
        @SerializedName("powerMeetingAccess")
        val powerMeetingAccess: Int,
        @SerializedName("rfidno")
        val rfidno: String
    )


}