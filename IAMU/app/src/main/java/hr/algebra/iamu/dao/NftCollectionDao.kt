package hr.algebra.iamu.dao

import androidx.room.*
import hr.algebra.iamu.model.NftCollection
import hr.algebra.iamu.model.NftItem


@Dao
interface NftCollectionDao {

    @Query("select * from nftcollection")
    fun getNftCollections(): MutableList<NftCollection>

    @Query("select * from nftcollection where _idNftCollection=:idToSearch")
    fun getNftCollection(idToSearch: Long): NftCollection?

    @Transaction
    @Insert
    fun insertNftCollection(nftCollection: NftCollection): Long

    @Update
    fun updateNftCollection(nftCollection: NftCollection)

    @Insert
    fun insertNftItems(nftItem: List<NftItem>)

    @Delete
    fun deleteCollection(nftCollection: NftCollection) //šaljemo cijelog persona jer radi s entitetima
}
