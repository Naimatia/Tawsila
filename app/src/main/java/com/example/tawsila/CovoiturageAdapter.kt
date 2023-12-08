import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tawsila.Covoiturage
import com.example.tawsila.R

class CovoiturageAdapter(
    private var originalCovoituragesList: List<Covoiturage>,
    private val context: Context
) : RecyclerView.Adapter<CovoiturageAdapter.ViewHolder>() {

    private var filteredCovoituragesList: List<Covoiturage> = originalCovoituragesList

    interface OnItemClickListener {
        fun onItemClick(covoiturage: Covoiturage)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_covoiturage_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val covoiturage = filteredCovoituragesList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(covoiturage)
        }

        holder.bind(covoiturage)
    }

    override fun getItemCount(): Int {
        return filteredCovoituragesList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val depart: TextView = itemView.findViewById(R.id.departTextView)
        val destination: TextView = itemView.findViewById(R.id.destinationTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val place: TextView = itemView.findViewById(R.id.placeTextView)
        val bagage: TextView = itemView.findViewById(R.id.bagageTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)


        fun bind(covoiturage: Covoiturage) {
            depart.text = covoiturage.depart ?: ""
            destination.text = covoiturage.destination ?: ""
            price.text = covoiturage.price?.toString() ?: ""
            place.text = covoiturage.place?.toString() ?: ""
            bagage.text = covoiturage.bagage ?: ""
            description.text = covoiturage.description ?: ""
            date.text = covoiturage.date ?: ""

        }
    }

    fun setFilteredData(newData: List<Covoiturage>) {
        filteredCovoituragesList = newData
        notifyDataSetChanged()
    }

    fun updateOriginalData(newData: List<Covoiturage>) {
        originalCovoituragesList = newData
        setFilteredData(newData) // Optionally update filtered data as well
    }
}

