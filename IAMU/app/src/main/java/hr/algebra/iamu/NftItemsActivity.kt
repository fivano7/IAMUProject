package hr.algebra.iamu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.iamu.databinding.ActivityNftItemsBinding
import hr.algebra.iamu.framework.startActivity
import hr.algebra.iamu.model.NftItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val COLLECTION_ID = "hr.algebra.iamu.collection_id"
class NftItemsActivity : AppCompatActivity() {

    private lateinit var nftItems: MutableList<NftItem>
    private lateinit var binding: ActivityNftItemsBinding

    private var nftItemPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0,0)

        binding = ActivityNftItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadNftItems()
        setupListeners()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //backButton dodamo

    }

    override fun onResume() {
        super.onResume()
        loadNftItems()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        binding.fbAdd.setOnClickListener {
            startActivity<EditNftItemActivity>(COLLECTION_ID, nftItemPosition)
        }
    }

    private fun loadNftItems() {

        //Glavna dretva
        GlobalScope.launch(Dispatchers.Main) {

            nftItemPosition = intent.getLongExtra(COLLECTION_ID, 0)

            //Asinkrono
            nftItems = withContext(Dispatchers.IO) {
                (applicationContext as App).getNftItemDao().getNftItemsForCollection(nftItemPosition)
            }

            //Glavna dretva
            binding.rvNftItems.apply {
                layoutManager = LinearLayoutManager(this@NftItemsActivity)
                adapter = NftItemAdapter(this@NftItemsActivity, nftItems)
            }

        }


    }

}