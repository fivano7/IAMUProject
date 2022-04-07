package hr.algebra.iamu.api

import retrofit2.Call
import retrofit2.http.GET

const val API_URL = "https://jsonkeeper.com/b/"
interface OpenseaApi {
    @GET("RBVV")

    fun fetchItems() : Call<List<OpenseaCollection>>
}