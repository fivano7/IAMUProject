package hr.algebra.iamu.model

import androidx.room.Embedded
import androidx.room.Relation

data class CollectionWithNftItems(
    @Embedded val nftCollection: NftCollection,

    @Relation(
        parentColumn = "_idNftCollection",
        entityColumn = "_nftCollectionParentId",
        entity = NftItem::class
    )
    val nftItems: List<NftItem>
)


