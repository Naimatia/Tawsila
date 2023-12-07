import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Cache instances based on their base URLs
    private val retrofitInstances: MutableMap<String, Retrofit> = mutableMapOf()

    // Function to get a Retrofit instance
    fun getClient(baseUrl: String): Retrofit {
        return retrofitInstances.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
