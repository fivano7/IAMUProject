package hr.algebra.iamu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.iamu.databinding.FragmentCollectionsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import hr.algebra.iamu.framework.startActivity
import kotlinx.coroutines.withContext

class CollectionsFragment : Fragment() {

    private lateinit var binding: FragmentCollectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCollections()
        setupListeners()
    }

    private fun setupListeners() {
        binding.fbAdd.setOnClickListener {
            requireContext().startActivity<EditNftCollectionActivity>()
        }

    }

    private fun loadCollections() {
        //Glavna dretva
        GlobalScope.launch(Dispatchers.Main) {

            //Asinkrono
            val collections = withContext(Dispatchers.IO) {
                //castamo applicationContext u app (kojeg smo sami napravili) gdje nam je getPersonDao metoda
                (context?.applicationContext as App).getNftCollectionDao().getNftCollections()
            }

            //Glavna dretva
            binding.rvCollections.apply {
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                adapter = CollectionAdapter(requireContext(), collections) //dodano this@ListFragment, šaljemo sebe kao navigableFragment jer ga implementiramo
            }
        }
    }
}