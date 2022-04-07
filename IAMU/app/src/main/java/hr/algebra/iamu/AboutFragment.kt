package hr.algebra.iamu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import hr.algebra.iamu.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnContactMe.setOnClickListener {
            activityResultLauncher.launch(arrayOf(android.Manifest.permission.CALL_PHONE))
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> //lamba zadnji parametar, callback
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse(getString(R.string.phone_number))
                    startActivity(callIntent)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
}



