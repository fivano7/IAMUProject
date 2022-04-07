package hr.algebra.iamu

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

class PreferencesFragment : PreferenceFragmentCompat() {

    private lateinit var appSettingsPrefs: SharedPreferences


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        appSettingsPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        appSettingsPrefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener())

    }

    override fun onResume() {
        super.onResume()
        appSettingsPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onSharedPreferenceChangeListener() =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (findPreference<Preference>(key)) {
                is SwitchPreferenceCompat -> {
                    if (key == context?.getString(R.string.dark_theme_key)) {
                        val isNightModeOn: Boolean =
                            appSettingsPrefs.getBoolean(context?.getString(R.string.dark_theme_key), false)
                        if (isNightModeOn) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            appSettingsPrefs.edit()
                                .putBoolean(context?.getString(R.string.dark_theme_key), true)
                                .apply()
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            appSettingsPrefs.edit()
                                .putBoolean(context?.getString(R.string.dark_theme_key), false)
                                .apply()
                        }
                    }
                }
            /*    is ListPreference -> {
                    // do sth else
                }*/
            }
        }


}