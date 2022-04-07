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
import hr.algebra.iamu.model.NftItem
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class NftItemAdapter(
    private val context: Context,
    private val nftItems: MutableList<NftItem>
) :
    RecyclerView.Adapter<NftItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        private val tvNftItemName = itemView.findViewById<TextView>(R.id.tvNftItemName)
        val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)

        fun bind(nftItem: NftItem) {
            tvNftItemName.text = nftItem.toString()

            Picasso.get()
                .load(File(nftItem.nFTImageUrl))
                .error(R.drawable.default_coll_img)
                .fit()
                .transform(RoundedCornersTransformation(360, 5))
                .into(ivImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.list_nft_items, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            context.startActivity<NftItemPagerActivity>(
                NFT_ITEM_ID,
                nftItems[position]._idNftItem!!,
                NFT_ITEM_POSITION,
                position.toLong()
            )
        }
        holder.itemView.setOnLongClickListener {
            context.startActivity<EditNftItemActivity>(NFT_ITEM_ID, nftItems[position]._idNftItem!!)
            true
        }

        holder.ivDelete.setOnClickListener {

            MaterialAlertDialogBuilder(
                context,
                R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
            ).apply {
                setTitle(context.getString(R.string.delete_nft))
                setMessage(context.getString(R.string.confirm_delete_nft))
                setIcon(R.drawable.trash)
                setCancelable(true)
                setPositiveButton(context.getString(R.string.yes)) { _, _ -> deleteNft(position) }
                setNegativeButton(context.getString(R.string.cancel), null)
                    .show()
            }
        }

        holder.bind(nftItems[position])
    }

    private fun AlertDialog.Builder.deleteNft(position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            //IO Thread
            withContext(Dispatchers.IO) {
                (context?.applicationContext as App).getNftItemDao()
                    .deleteNftItem(nftItems[position])
            }
            nftItems.removeAt(position) //iz liste brišemo
            notifyDataSetChanged() //updatemo recyclerview
        }
        Toast.makeText(context, context.getString(R.string.nft_deleted), Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = nftItems.size
}