package hr.algebra.iamu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import hr.algebra.iamu.framework.startActivityForSharing
import hr.algebra.iamu.framework.startActivityForUrl
import hr.algebra.iamu.model.NftCollection
import hr.algebra.iamu.model.NftItem
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

private const val MIME_TYPE = "text/plain"
class NftItemsPagerAdapter(
    private val context: Context,
    private val nftItems: MutableList<NftItem>,
    private val nftCollection: NftCollection
) : RecyclerView.Adapter<NftItemsPagerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivNftItem = itemView.findViewById<ImageView>(R.id.ivNftItem)
        private val tvNftItemName = itemView.findViewById<TextView>(R.id.tvNftItemName)

        val btnCheckItemOnOpensea = itemView.findViewById<MaterialButton>(R.id.btnCheckItemOnOpensea)
        val btnCheckCollectionOnOpensea =
            itemView.findViewById<MaterialButton>(R.id.btnCheckCollectionOnOpensea)

        private var tvCollectionName = itemView.findViewById<TextView>(R.id.tvCollectionName)
        private val ivCollection = itemView.findViewById<ImageView>(R.id.ivNftCollection)
        private val tvCollectionDescription =
            itemView.findViewById<TextView>(R.id.tvCollectionDescription)

        fun bind(nftItem: NftItem, nftCollection: NftCollection) {
            tvNftItemName.text = nftItem.nFTName
            tvCollectionName.text = nftCollection.collectionName
            tvCollectionDescription.text = nftCollection.collectionDescription

            Picasso.get()
                .load(File(nftItem.nFTImageUrl))
                .error(R.drawable.gfc_logo)
                .into(ivNftItem)

            Picasso.get()
                .load(File(nftCollection.collectionImageUrl))
                .error(R.drawable.gfc_logo)
                .fit()
                .transform(RoundedCornersTransformation(360, 5))
                .into(ivCollection)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.nft_item_pager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.btnCheckItemOnOpensea.setOnClickListener {
            context.startActivityForUrl(nftItems[position].nFTOpenseaUrl)
        }

        holder.btnCheckItemOnOpensea.setOnLongClickListener {
            context.startActivityForSharing(nftItems[position].nFTOpenseaUrl, MIME_TYPE)
            true
        }

        holder.btnCheckCollectionOnOpensea.setOnClickListener {
            context.startActivityForUrl(nftCollection.collectionOpenseaUrl)
        }

        holder.btnCheckCollectionOnOpensea.setOnLongClickListener {
            context.startActivityForSharing(nftCollection.collectionOpenseaUrl, MIME_TYPE)
            true
        }

        holder.bind(nftItems[position], nftCollection)
    }

    override fun getItemCount() = nftItems.size
}