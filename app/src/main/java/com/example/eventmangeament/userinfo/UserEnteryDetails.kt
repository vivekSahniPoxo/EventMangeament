package com.example.eventmangeament.userinfo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.SimpleTimeZone

@Entity(tableName = "user_entry_details")
data class UserEntryDetails(@PrimaryKey(autoGenerate = true)
    val Id:Int,
    var epcNo:String,
    var category:String
    )


@Entity(tableName = "sync_entry_details")
data class SyncEntryDetails(@PrimaryKey(autoGenerate = true)
                            val Id:Int,
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
                            val rfidno: String)





//                            @SerializedName("accsesPoint")
//                            val accsesPoint: String,
//                            @SerializedName("category")
//                            val category: String,
//                            @SerializedName("company")
//                            val company: String,
//                            @SerializedName("image")
//                            val image: String,
//                            @SerializedName("isexhibition")
//                            val isexhibition: String,
//                            @SerializedName("isLunch")
//                            val isfood: String,
//                            @SerializedName("ispowertimezone")
//                            val ispowertimezone: String,
//                            @SerializedName("name")
//                            val name: String,
//                            @SerializedName("rfidNo")
//                            val rfidNo: String)


@Entity(tableName = "all_scanned_details",indices = [Index(value = ["rfidNumber"], unique = true)])
data class AllScannedDetails(@PrimaryKey(autoGenerate = true)
                              val id:Int,
                             @SerializedName("DinnerAccess")
                             var dinnerAccess: Int,
                             @SerializedName("EventAccess")
                             var eventAccess: Int,
                             @SerializedName("FullAccess")
                             var fullAccess: Int,
                             @SerializedName("LunchAccess")
                             var lunchAccess: Int,
                             @SerializedName("PowerMeetingAccess")
                             var powerMeetingAccess: Int,
                             @SerializedName("RfidNumber")
                             val rfidNumber: String)



@Entity(tableName = "sync_entry_details_two")
data class TempData(@PrimaryKey(autoGenerate = true)
                            val Id:Int,
                            @SerializedName("category")
                            val category: String)

