package hr.algebra.iamu.dao

import androidx.room.*
import hr.algebra.iamu.model.NftItem

@Dao
interface NftItemDao {

    @Query("select * from nftitem")
    fun getNftItem(): MutableList<NftItem>

    @Query("select * from nftitem where _nftCollectionParentId=:idToSearch")
    fun getNftItemsForCollection(idToSearch: Long): MutableList<NftItem>

    @Query("select * from nftitem where _idNftItem=:idToSearch")
    fun getNftItem(idToSearch: Long): NftItem?

    @Insert
    fun insertNftItem(nftItem: NftItem)

    @Update
    fun updateNftItem(nftItem: NftItem)

    @Delete
    fun deleteNftItem(nftItem: NftItem)

}
