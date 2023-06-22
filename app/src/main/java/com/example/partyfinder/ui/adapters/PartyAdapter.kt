import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.partyfinder.R
import com.example.partyfinder.models.Party
import com.example.partyfinder.utils.CustomTextView
import com.example.partyfinder.utils.CustomTextViewBold
import com.example.partyfinder.utils.GlideLoader

class PartyAdapter(private val partyList: List<Party>) :
    RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_party, parent, false)
        return PartyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        val party = partyList[position]
        holder.bind(party)
    }

    override fun getItemCount(): Int {
        return partyList.size
    }

    inner class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val partyTitleImageView: ImageView =
            itemView.findViewById(R.id.iv_party_image)
        private val partyNameTextView: CustomTextViewBold =
            itemView.findViewById(R.id.tv_party_name)
        private val partyAddressTextView: CustomTextView =
            itemView.findViewById(R.id.tv_party_address)
        private val partyDateTextView: CustomTextView =
            itemView.findViewById(R.id.tv_party_date)

        fun bind(party: Party) {

            GlideLoader(itemView.context).loadPicture(party.image, partyTitleImageView)
            partyNameTextView.text = party.name
            partyAddressTextView.text = party.address
            partyDateTextView.text = party.date
        }
    }
}