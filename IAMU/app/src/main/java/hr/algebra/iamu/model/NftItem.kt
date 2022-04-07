package hr.algebra.iamu.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "nftitem",
    foreignKeys = [
        ForeignKey(
            entity = NftCollection::class,
            parentColumns = ["_idNftCollection"],
            childColumns = ["_nftCollectionParentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NftItem(
    @PrimaryKey(autoGenerate = true) var _idNftItem: Long? = null,
    var _nftCollectionParentId: Long? = null,
    var nFTName: String? = null,
    var nFTImageUrl: String? = null,
    var nFTOpenseaUrl: String? = null
) {
    override fun toString() = "$nFTName"
}
