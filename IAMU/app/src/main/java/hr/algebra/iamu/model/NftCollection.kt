package hr.algebra.iamu.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "nftcollection")
data class NftCollection(
    @PrimaryKey(autoGenerate = true) var _idNftCollection: Long? = null,
    var collectionName: String? = null,
    var collectionImageUrl: String? = null,
    var collectionBannerUrl: String? = null,
    var collectionOpenseaUrl: String? = null,
    var collectionDescription: String? = null
) {
    override fun toString() = "$collectionName"
}
