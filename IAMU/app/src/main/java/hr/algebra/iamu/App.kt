package hr.algebra.iamu

import android.app.Application
import hr.algebra.iamu.dao.NftCollectionDao
import hr.algebra.iamu.dao.IamuDatabase
import hr.algebra.iamu.dao.NftItemDao

class App : Application() {
    private lateinit var nftCollectionDao: NftCollectionDao
    private lateinit var nftItemDao: NftItemDao

    override fun onCreate() {
        super.onCreate()
        var db = IamuDatabase.getInstance(this)
        nftCollectionDao = db.nftCollectionDao()
        nftItemDao = db.nftItemDao()
    }

    fun getNftCollectionDao() = nftCollectionDao
    fun getNftItemDao() = nftItemDao
}