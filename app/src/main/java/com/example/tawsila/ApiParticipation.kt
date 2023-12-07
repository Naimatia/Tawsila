package com.example.tawsila

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiParticipation {


    companion object {
        const val BASE_URL = "http://192.168.56.1:3002"

    }
    //   @GET("/auth/all")
    //   fun getAllUsers(): Call<List<UserApp>>



    @POST("/participation")
    fun postParticipation(@Body participationRequest: ParticipationRequest): Call<ResponseBody>
}