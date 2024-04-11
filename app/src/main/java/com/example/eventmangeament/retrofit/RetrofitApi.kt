package com.example.eventmangeament.retrofit

import com.example.eventmangeament.userinfo.GetDataFromApi
import com.example.eventmangeament.userinfo.PushRequestBody
import com.example.eventmangeament.userinfo.RfidRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface RetrofitApi {


    @GET("sync")
   // fun getAllData(): Call<GetDataFromApi>
    fun getAllData(@Query("page") page:Int , @Query("pageSize") pageSize:Int ): Call<GetDataFromApi>

    @POST("push-data")
    fun pushDataOnServer(@Body pushRequestBody: ArrayList<RfidRequestBody>): Call<String>

        @GET("sync")
        suspend fun getSyncs(@Query("page") page: Int, @Query("pageSize") pageSize: Int): List<GetDataFromApi>




}