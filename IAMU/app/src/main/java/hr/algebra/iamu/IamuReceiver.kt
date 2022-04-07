 package hr.algebra.iamu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.iamu.framework.startActivity

 class IamuReceiver : BroadcastReceiver() {

     //prima context
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity<HostActivity>()
    }
}