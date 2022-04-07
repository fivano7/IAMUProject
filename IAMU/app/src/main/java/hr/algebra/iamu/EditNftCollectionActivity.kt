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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import hr.algebra.iamu.databinding.ActivityEditNftCollectionBinding
import hr.algebra.iamu.framework.startActivity
import hr.algebra.iamu.model.NftCollection
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


const val IMAGE_TYPE = "image/*"

class EditNftCollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNftCollectionBinding
    private lateinit var nftCollection: NftCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        binding = ActivityEditNftCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //backButton dodamo

        fetchNftCollection()
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


    private fun fetchNftCollection() {
        val nftCollectionId = intent?.getLongExtra(COLLECTION_ID, 0)

        if (nftCollectionId != null) {
            GlobalScope.launch(Dispatchers.Main) {

                //ASINKRONO
                nftCollection = withContext(Dispatchers.IO) {
                    (applicationContext as App).getNftCollectionDao()
                        .getNftCollection(nftCollectionId) ?: NftCollection()
                }

                bindNftCollection()
            }
        } else {
            nftCollection = NftCollection()
            bindNftCollection()
        }
    }

    private fun bindNftCollection() {
        binding.etNftCollectionName.setText(nftCollection.collectionName ?: "")
        binding.etOpenSeaLink.setText(nftCollection.collectionOpenseaUrl ?: "")
        binding.etDescription.setText(nftCollection.collectionDescription ?: "")

        if (nftCollection.collectionImageUrl != null) {
            Picasso.get()
                .load(File(nftCollection.collectionImageUrl))
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

        if (nftCollection.collectionBannerUrl != null) {
            Picasso.get()
                .load(File(nftCollection.collectionBannerUrl))
                .into(binding.ivBanner)
        } else {
            Picasso.get()
                .load(R.drawable.default_header)
                .into(binding.ivBanner)
        }
    }

    private fun setupListeners() {
        binding.ivImage.setOnLongClickListener {
            handleImage()
            true
        }
        binding.ivBanner.setOnLongClickListener {
            handleBanner()
            true
        }
        binding.btnCommit.setOnClickListener {
            if (formValid()) {
                commit()
            }
        }
        binding.etNftCollectionName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nftCollection.collectionName = text?.toString()?.trim()
            }
        })
        binding.etOpenSeaLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nftCollection.collectionOpenseaUrl = text?.toString()?.trim()
            }
        })
        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nftCollection.collectionDescription = text?.toString()?.trim()
            }
        })

    }

    private fun commit() {
        GlobalScope.launch(Dispatchers.Main) {

            withContext(Dispatchers.IO) {
                if (nftCollection._idNftCollection == null) {
                    (applicationContext as App).getNftCollectionDao().insertNftCollection(nftCollection)
                } else {
                    (applicationContext as App).getNftCollectionDao().updateNftCollection(nftCollection)
                }
            }
        }
        startActivity<HostActivity>()
    }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etNftCollectionName, binding.etOpenSeaLink, binding.etDescription).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_value)
                ok = false
            }
        }

        arrayOf(nftCollection.collectionImageUrl, nftCollection.collectionBannerUrl).forEach {
            if (it.isNullOrEmpty()){
                Toast.makeText(this, getString(R.string.collection_pictures_required), Toast.LENGTH_SHORT).show()
                ok = false
            }
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

    private fun handleBanner() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            bannerResult.launch(this)
        }
    }


    private val imageResult: ActivityResultLauncher<Intent> =

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (nftCollection.collectionImageUrl != null) {
                    File(nftCollection.collectionImageUrl!!).delete()
                }
                val dir = this.applicationContext?.getExternalFilesDir(null)
                val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + getString(R.string.jpg))
                this.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        nftCollection.collectionImageUrl = file.absolutePath
                        bindNftCollection()
                    }
                }
            }

        }

    private val bannerResult: ActivityResultLauncher<Intent> =

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (nftCollection.collectionBannerUrl != null) {
                    File(nftCollection.collectionBannerUrl!!).delete()
                }
                val dir = this.applicationContext?.getExternalFilesDir(null)
                val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + getString(R.string.jpg))
                this.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        nftCollection.collectionBannerUrl = file.absolutePath
                        bindNftCollection()
                    }
                }
            }

        }

}