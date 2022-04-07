package hr.algebra.iamu.api

import com.google.gson.annotations.SerializedName

data class OpenseaNftItem(
    @SerializedName("NFTName") val nFTName : String,
    @SerializedName("NFTImageUrl") val nFTImageUrl : String,
    @SerializedName("NFTOpenseaUrl") val nFTOpenseaUrl : String
)
