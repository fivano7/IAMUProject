package hr.algebra.iamu.api

import com.google.gson.annotations.SerializedName

data class OpenseaCollection(
    @SerializedName("CollectionName") val collectionName : String,
    @SerializedName("CollectionImageUrl") val collectionImageUrl : String,
    @SerializedName("CollectionBannerUrl") val collectionBannerUrl : String,
    @SerializedName("CollectionOpenseaUrl") val collectionOpenseaUrl : String,
    @SerializedName("CollectionDescription") val collectionDescription : String,
    @SerializedName("NFTsOfCollection") val nFTsOfCollection : List<OpenseaNftItem>
)
