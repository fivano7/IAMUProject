package hr.algebra.iamu

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import hr.algebra.iamu.databinding.ActivityStartBinding
import hr.algebra.iamu.framework.callDelayed
import hr.algebra.iamu.framework.getBooleanPreference
import hr.algebra.iamu.framework.startActivity
import hr.algebra.iamu.framework.startAnimation

private const val DELAY = 3000L
const val DATA_IMPORTED = "hr.algebra.iamu.data_imported"
class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()
        setupListeners()
        redirect()

    }

    private fun setupListeners() {
        binding.btnRetry.setOnClickListener{
            redirect()
        }
    }

    private fun redirect() {
        //EXTENSION
        if (getBooleanPreference(DATA_IMPORTED)) {
            //EXTENSION
            callDelayed(DELAY) { startActivity<HostActivity>() }
        } else {
            if (isOnline()) {
                binding.tvInfo.text = getString(R.string.fetching_data)
                binding.btnRetry.visibility = View.INVISIBLE
                //StartActivity -> IamuService -> (Iamu)BroadCastReceiver
                Intent(this, IamuService::class.java).apply {
                    IamuService.enqueue(
                        this@StartActivity,
                        this
                    )
                }
            } else {
                binding.tvInfo.text = getString(R.string.no_internet)
                binding.btnRetry.visibility = View.VISIBLE
            }
        }
    }

    private fun isOnline(): Boolean {

        //Slična kao ekstenzijska funkcija sa vježbi
        val connectivityManager = getSystemService<ConnectivityManager>()

        connectivityManager?.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }
        return false
    }

    private fun prepareView() {
        supportActionBar?.hide()
        hideSystemUI()
        startAnimations()
        handleNightMode()
    }

    private fun handleNightMode() {
        val appSettingPrefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean(getString(R.string.dark_theme_key), false)

        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun startAnimations() {
        //EXTENSION
        binding.ivLogo.startAnimation(R.anim.rotate)
        binding.tvInfo.startAnimation(R.anim.blink)
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView.findViewById(R.id.content))
            .let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
    }
}