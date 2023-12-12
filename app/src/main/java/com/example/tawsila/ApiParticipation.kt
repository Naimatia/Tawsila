import com.example.tawsila.ParticipationRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiParticipation {

    companion object {
        const val BASE_URL = "http://172.20.10.3:3002"
    }


    @POST
    fun postReservation(@Url url: String): Call<ResponseBody>

    @GET
    fun  getFilteredReservations(@Url url: String): Call<ResponseBody>

    @GET("/p/participants/cov/{covoiturageId}")
    fun getParticipantsForCovoiturage(@Path("covoiturageId") covoiturageId: Long): Call<List<ParticipationRequest>>
}
