package hr.algebra.iamu

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import hr.algebra.iamu.api.OpenseaFetcher

//Dodati service u manifest
private const val JOB_ID = 1
class IamuService : JobIntentService() {

    //Background metoda
    override fun onHandleWork(intent: Intent) {

        OpenseaFetcher(this).fetchItems()
        //setBooleanPreference(DATA_IMPORTED, true)


        //EXTENSION
        //sendBroadcast<IamuReceiver>()
        //sendBroadcast(Intent(this, IamuReceiver::class.java))
    }

    //Statička metoda, prvo se poziva ona, ručno, i nakon nje se automatski poziva onHandleWork
    companion object{
        //treba isto primiti context jer je statička metoda
        fun enqueue(context: Context, intent: Intent) {
            enqueueWork(context, IamuService::class.java, JOB_ID, intent)
        }
    }
}