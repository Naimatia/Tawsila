package com.example.tawsila

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface MicroServiceApi {

    companion object {
        const val BASE_URL = "http://169.254.142.86:8080"
        const val BASE_URLF = "http://169.254.142.86:3002"

    }


    @POST("/auth/register")
    fun registerUser(@Body userDTO: UserDTO): Call<String>

    // @POST("/auth/token")

    @POST("/auth/token")
    fun getToken(@Body authRequest: AuthRequest): Call<Map<String, Any>>



    @GET("/auth/validate")
    fun validateToken(@Query("token") token: String): Call<String>

    @GET("/auth/admin")
    fun getAdminEndpoint(): Call<String>

    @POST("/auth/upload_image/{id}")
    @Multipart
    fun setImageById(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Call<String>

    @GET("/auth/{id}")
    fun getUserInfo(@Path("id") userId: Long): Call<UserDTO>

    @PUT("/auth/{id}")
    fun updateUser(@Path("id") userId: Long, @Body updatedUser: UserDTO): Call<String>

    @GET("/auth/drivers")
    fun getDrivers(): Call<List<ClientDTO>>
    @DELETE("/auth/{id}")
    fun deleteUserById(@Path("id") userId: Long): Call<String>
    @GET("/auth/clients")
    fun getClients(): Call<List<ClientDTO>>
    @GET("/driver/covoiturages")
    fun getCovs(): Call<List<Covoiturage>>

    @GET
    fun getFilteredCovoiturages(
        @Url url: String,

        ): Call<List<Covoiturage>>


    @GET
    fun getFilteredReservations(
        @Url url: String,

        ): Call<List<Reservation>>

    @GET
    fun getCovoiturageDetails(
        @Url url: String,

        ): Call<Covoiturage>
    @DELETE
    fun deleteCovoiturage(@Url url: String): Call<ResponseBody>

    @GET
    fun getParticipantInfo( @Url url: String, ): Call<UserDTO>

    @PUT
    fun UpdateReservations( @Url url: String,
    ):  Call<Reservation>

    @POST
    fun sendEmail(@Url url: String): Call<Reservation>
    @DELETE

    fun DeleteReservations( @Url url: String,
    ):  Call<Reservation>
    //   @POST("/participation")
    //  fun postParticipation(@Body participationRequest: ParticipationRequest): Call<ResponseBody>
}