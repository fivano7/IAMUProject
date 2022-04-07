package hr.algebra.iamu

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import hr.algebra.iamu.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        /*overridePendingTransition(0,0)*/

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        initHamburgerMenu()
    }

    private fun initHamburgerMenu() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                toggleDrawer()
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initNavigation() {
        var navController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
    }
}