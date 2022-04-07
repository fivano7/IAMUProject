package hr.algebra.iamu.api

import android.content.Context
import android.util.Log
import hr.algebra.iamu.App
import hr.algebra.iamu.DATA_IMPORTED
import hr.algebra.iamu.IamuReceiver
import hr.algebra.iamu.framework.sendBroadcast
import hr.algebra.iamu.framework.setBooleanPreference
import hr.algebra.iamu.handler.downloadImageAndStore
import hr.algebra.iamu.model.NftCollection
import hr.algebra.iamu.model.NftItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenseaFetcher(private val context: Context) {

    private var openseaApi: OpenseaApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openseaApi = retrofit.create(OpenseaApi::class.java)
        //otvorit connection, otić u drugu dretu, dovuc podatke, konvertirat ih u liste opensea collectiona -> sve to ovo radi
    }

    fun fetchItems() {
        //ovo vraća pointer na asinkronost. Kad se obavi spremi se u "request"
        val request = openseaApi.fetchItems() //ovo dinamički generira klasu preko Retrofit2
        request.enqueue(object : Callback<List<OpenseaCollection>> {
            override fun onResponse(
                call: Call<List<OpenseaCollection>>,
                response: Response<List<OpenseaCollection>>
            ) {
                //response ne može bit null, ali body u njemu može biti null
                response.body()?.let {
                    populateItems(it) //proći će kroz sve OpenseaCollection Iteme i pretvorit ih u Collection iteme
                }
            }

            override fun onFailure(call: Call<List<OpenseaCollection>>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }

        }) //prima callback, kao poziv nazad. Ovo je anonimna implementacija interfacea
    }

    private fun populateItems(openseaCollections: List<OpenseaCollection>) {
        //OVDJE SMO U FOREGROUND THREADU PO DEFAULTU, A TREBAMO SKINUT SLIKU PA MORAMO OPET U BACKGROUND THREAD

        //POZIV POZADINSKOG THREADA, TAKO DA SE SVE ODVIJA U POZADINSKOJ DRETVI
        GlobalScope.launch {
            openseaCollections.forEach {
                //metoda downloadImageAndStore će spremiti file lokalno i dati nam lokalnu putanju
                val picturePathImage = downloadImageAndStore(
                    context,
                    it.collectionImageUrl,
                    it.collectionName.replace(" ", "_") + "CIMG"
                )

                val picturePathBanner = downloadImageAndStore(
                    context,
                    it.collectionBannerUrl,
                    it.collectionName.replace(" ", "-") + "CBAN"
                )

                val nftItems = mutableListOf<NftItem>()
                for (nftItem in it.nFTsOfCollection) {

                    val picturePathNftImage = downloadImageAndStore(
                        context,
                        nftItem.nFTImageUrl,
                        nftItem.nFTName.replace(" ", "_") + "NIMG"
                    )

                    nftItems.add(
                        NftItem(
                            null,
                            null,
                            nftItem.nFTName,
                            picturePathNftImage ?: "",
                            nftItem.nFTOpenseaUrl,
                        )
                    )
                }

                val nftCollection = NftCollection(
                    null,
                    it.collectionName,
                    picturePathImage ?: "", //ako je null stavi prazan string
                    picturePathBanner ?: "",
                    it.collectionOpenseaUrl,
                    it.collectionDescription
                    )

                //druga dretva
                withContext(Dispatchers.IO) {
                    val insertNftCollection: Long = (context?.applicationContext as App).getNftCollectionDao().insertNftCollection(nftCollection)

                    nftItems.forEach { item -> item._nftCollectionParentId = insertNftCollection }

                    (context?.applicationContext as App).getNftCollectionDao().insertNftItems(nftItems)
                }

            }

            //ovo je prije bilo u IamuService, ali trea biti ovdje zbog asinkronosti
            context.setBooleanPreference(DATA_IMPORTED, true)
            context.sendBroadcast<IamuReceiver>() //ovdje se vraćamo u IamuReceiver koji će nam promijeniti "ekran", reciever je u glavnoj dretvi
        }
    }


}