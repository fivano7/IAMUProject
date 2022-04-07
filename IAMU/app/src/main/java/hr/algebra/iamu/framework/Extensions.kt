package hr.algebra.iamu.framework

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.preference.PreferenceManager
import hr.algebra.iamu.R

fun View.startAnimation(animationId: Int) =
    startAnimation(AnimationUtils.loadAnimation(context, animationId))

inline fun <reified T : Activity> Context.startActivity() =
    startActivity(
        Intent(this, T::class.java)
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    )

inline fun <reified T : Activity> Context.startActivity(key: String, value: Long) =
    startActivity(
        Intent(this, T::class.java)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(key, value)
            }
    )

inline fun <reified T : Activity> Context.startActivity(
    key1: String,
    value1: Long,
    key2: String,
    value2: Long
) =
    startActivity(
        Intent(this, T::class.java)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(key1, value1)
                putExtra(key2, value2)
            }
    )

inline fun Context.startActivityForUrl(urlToStart: String?) {
    val url = urlToStart ?: ""

    if (url.startsWith(getString(R.string.https)) || url.startsWith(getString(R.string.http))) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } else {
        Toast.makeText(this, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show()
    }
}

inline fun Context.startActivityForSharing(contentToShare: String?, mimeType: String) {

    contentToShare?.let {
        Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_TEXT, contentToShare)
        }
            .also {
                startActivity(Intent.createChooser(it, getString(R.string.share)))
            }
    }
}

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() =
    sendBroadcast(Intent(this, T::class.java))

fun Context.setBooleanPreference(key: String, value: Boolean) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()

fun Context.getBooleanPreference(key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false) //false je defaultValue

//Slično kao Platform.runLater
fun callDelayed(delay: Long, function: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(function, delay)
}