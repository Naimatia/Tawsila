import com.example.tawsila.ParticipationRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiParticipation {

    companion object {
        const val BASE_URL = "http://169.254.142.86:3002"
    }


    @POST
    fun postReservation(@Url url: String): Call<ResponseBody>


}
