package hr.algebra.iamu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->

        val atmIlica = LatLng(45.812551, 15.950579)
        val atmAdmiral = LatLng(45.794759, 15.919260)
        val atmBranimirova = LatLng(45.806987, 15.994778)

        googleMap.addMarker(MarkerOptions().position(atmIlica).title(getString(R.string.btc_atm_ilica)))
        googleMap.addMarker(MarkerOptions().position(atmAdmiral).title(getString(R.string.btc_atm_rudeska)))
        googleMap.addMarker(MarkerOptions().position(atmBranimirova).title(getString(R.string.btc_atm_branimirova)))

        val zoomLevel = 12.0f //do 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atmIlica, zoomLevel))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}