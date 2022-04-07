package hr.algebra.iamu

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import hr.algebra.iamu.databinding.ActivityEditNftItemBinding
import hr.algebra.iamu.model.NftItem
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

const val NFT_ITEM_ID = "hr.algebra.iamu.nft_item_id"
class EditNftItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNftItemBinding
    private lateinit var nftItem: NftItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        binding = ActivityEditNftItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //backButton dodamo

        fetchNftItem()
        setupListeners()
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

    private fun fetchNftItem() {

        val nftItemId = intent?.getLongExtra(NFT_ITEM_ID, 0)


        if (nftItemId != null) {
            GlobalScope.launch(Dispatchers.Main) {

                //ASINKRONO
                nftItem = withContext(Dispatchers.IO) {
                    (applicationContext as App).getNftItemDao().getNftItem(nftItemId) ?: NftItem()
                }
                bindNftItem()
            }
        } else {
            nftItem = NftItem()
            bindNftItem()
        }
    }

    private fun bindNftItem() {
        binding.etNftItemName.setText(nftItem.nFTName ?: "")
        binding.etOpenSeaLink.setText(nftItem.nFTOpenseaUrl ?: "")

        if (nftItem.nFTImageUrl != null) {
            Picasso.get()
                .load(File(nftItem.nFTImageUrl))
                .fit()
                .transform(RoundedCornersTransformation(360, 5))
                .into(binding.ivImage)
        } else {
            Picasso.get()
                .load(R.drawable.default_coll_img)
                .fit()
                .transform(RoundedCornersTransformation(360, 5))
                .into(binding.ivImage)
        }
    }

    private fun setupListeners() {
        binding.ivImage.setOnLongClickListener {
            handleImage()
            true
        }

        binding.btnCommit.setOnClickListener {
            if (formValid()) {
                commit()
            }
        }
        binding.etNftItemName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nftItem.nFTName = text?.toString()?.trim()
            }
        })
        binding.etOpenSeaLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nftItem.nFTOpenseaUrl = text?.toString()?.trim()
            }
        })
    }

    private fun commit() {
        val nftCollectionId = intent?.getLongExtra(COLLECTION_ID, 0)

        GlobalScope.launch(Dispatchers.Main) {

            withContext(Dispatchers.IO) {
                if (nftItem._idNftItem == null) {
                        nftItem._nftCollectionParentId = nftCollectionId
                    (applicationContext as App).getNftItemDao().insertNftItem(nftItem)
                } else {
                    (applicationContext as App).getNftItemDao().updateNftItem(nftItem)
                }
            }
        }
        finish()
        //startActivity<NftItemsActivity>(COLLECTION_ID, nftCollectionId!!.toLong())
    }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etNftItemName, binding.etOpenSeaLink).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_value)
                ok = false
            }
        }

        if (nftItem.nFTImageUrl.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.nft_image_required), Toast.LENGTH_SHORT).show()
            ok = false
        }

        return ok
    }

    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            imageResult.launch(this)
        }
    }
    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (nftItem.nFTImageUrl != null) {
                    File(nftItem.nFTImageUrl ).delete()
                }
                val dir = applicationContext.getExternalFilesDir(null)
                val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + getString(R.string.jpg))
                this.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        nftItem.nFTImageUrl = file.absolutePath
                        bindNftItem()
                    }
                }
            }

        }


}