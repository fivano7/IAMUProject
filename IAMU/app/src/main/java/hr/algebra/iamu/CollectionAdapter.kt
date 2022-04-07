package hr.algebra.iamu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import hr.algebra.iamu.framework.startActivity
import hr.algebra.iamu.model.NftCollection
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class CollectionAdapter(
    private val context: Context,
    private val collections: MutableList<NftCollection>
) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        private val ivBanner = itemView.findViewById<ImageView>(R.id.ivBanner)
        private val tvCollectionName = itemView.findViewById<TextView>(R.id.tvCollectionName)
        val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)

        fun bind(nftCollection: NftCollection) {
            tvCollectionName.text = nftCollection.toString()

            Picasso.get()
                .load(File(nftCollection.collectionImageUrl))
                .error(R.drawable.gfc_logo)
                .fit()
                .transform(RoundedCornersTransformation(360, 5))
                .into(ivImage)

            Picasso.get()
                .load(File(nftCollection.collectionBannerUrl))
                .error(R.drawable.header)
                .into(ivBanner)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            itemView = LayoutInflater.from(context).inflate(R.layout.list_collection, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnLongClickListener {

          context.startActivity<EditNftCollectionActivity>(
                COLLECTION_ID,
                collections[position]._idNftCollection!!
          )


            true
        }

        holder.itemView.setOnClickListener {
            context.startActivity<NftItemsActivity>(
                COLLECTION_ID,
                collections[position]._idNftCollection!!
            )
        }

        holder.ivDelete.setOnClickListener {

            MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog).apply {
                setTitle(context.getString(R.string.delete_collection))
                setMessage(context.getString(R.string.confirm_delete_collection))
                setIcon(R.drawable.trash)
                setCancelable(true)
                setPositiveButton(context.getString(R.string.yes)) { _, _ -> deleteCollection(position) }
                setNegativeButton(context.getString(R.string.cancel), null)
                .show()
            }

        }

        holder.bind(collections[position])
    }

    private fun AlertDialog.Builder.deleteCollection(position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            //IO Thread
            withContext(Dispatchers.IO) {
                (context?.applicationContext as App).getNftCollectionDao()
                    .deleteCollection(collections[position])
            }
            collections.removeAt(position) //iz liste brišemo
            notifyDataSetChanged() //updatemo recyclerview
        }
        Toast.makeText(context, context.getString(R.string.collection_deleted), Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = collections.size
}