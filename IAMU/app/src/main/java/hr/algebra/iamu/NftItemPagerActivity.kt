package hr.algebra.iamu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.iamu.databinding.ActivityNftItemPagerBinding
import hr.algebra.iamu.model.NftCollection
import hr.algebra.iamu.model.NftItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val NFT_ITEM_POSITION = "hr.algebra.iamu.nft_item_position"
class NftItemPagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNftItemPagerBinding

    private lateinit var nftItems: MutableList<NftItem>
    private lateinit var nftCollection: NftCollection

    private var nftItemIndex = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNftItemPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPager()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //backButton dodamo
    }

    //za back kad stisnemo na back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initPager() {

        //Glavna dretva
        GlobalScope.launch(Dispatchers.Main) {

            nftItemIndex = intent.getLongExtra(NFT_ITEM_ID, 0) //nft_item_id
            var position = intent.getLongExtra(NFT_ITEM_POSITION, 0) //bez ovoga ne znam kako doći do pozicije


            //nft item koji služi da iz njega izvadimo CollectionID koji nam treba za vaditi podatke o collectionima
            var nftItem = withContext(Dispatchers.IO) {
                (applicationContext as App).getNftItemDao()
                    .getNftItem(nftItemIndex)
            }

            //Itemi koji se prikazuju
            nftItems = withContext(Dispatchers.IO) {
                (applicationContext as App).getNftItemDao()
                    .getNftItemsForCollection(nftItem!!._nftCollectionParentId!!)
            }

            //dohvaćamo kolekciju kojoj pripada item, zbog ispisa informacija
            nftCollection = withContext(Dispatchers.IO) {
                (applicationContext as App).getNftCollectionDao()
                    .getNftCollection(nftItem?._nftCollectionParentId!!)!!
            }

            binding.nftItemViewPager.adapter =
                NftItemsPagerAdapter(this@NftItemPagerActivity, nftItems, nftCollection)
            binding.nftItemViewPager.currentItem = position.toInt()
        }

    }
}