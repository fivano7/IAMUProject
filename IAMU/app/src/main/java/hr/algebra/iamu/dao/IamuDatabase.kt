package hr.algebra.iamu.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hr.algebra.iamu.model.NftCollection
import hr.algebra.iamu.model.NftItem


@Database(entities = [NftCollection::class, NftItem::class], version = 1, exportSchema = true)
abstract class IamuDatabase : RoomDatabase() {
    abstract fun nftCollectionDao() : NftCollectionDao
    abstract fun nftItemDao() : NftItemDao

    //SINGLETON, LAZY
    companion object {
        @Volatile //ne radi sa cacheom nego sve čita direktno iz memorije
        private var INSTANCE: IamuDatabase? = null

        fun getInstance(context: Context) =
            //Ako nema instance onda je kreiraj -> elvisOperator. Sinkroniziramo zbog threadova
            INSTANCE ?: synchronized(IamuDatabase::class.java) { //"ako je ovo null kreni raditi"
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it } //"ako je to null napravi" (double checked locking)
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            IamuDatabase::class.java,
            "iamuProjectDatabase.db"
        ).build()

    }
}