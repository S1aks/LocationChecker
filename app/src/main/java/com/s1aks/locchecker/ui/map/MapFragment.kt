package com.s1aks.locchecker.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.s1aks.locchecker.R
import com.s1aks.locchecker.databinding.FragmentMapBinding
import com.s1aks.locchecker.domain.entities.MapPosition
import com.s1aks.locchecker.ui.base.BaseFragment
import com.s1aks.locchecker.ui.edit_dialog.EditMarkerDialogFragment
import com.s1aks.locchecker.ui.edit_dialog.OnSaveClickListener
import com.s1aks.locchecker.ui.markers.MarkersFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import kotlin.concurrent.thread

class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    OnMapReadyCallback, LocationListener, OnSaveClickListener {

    private var markerId: Int = -1
    private var geoString: String? = null
    private val mapViewModel: MapViewModel by viewModel()
    private var map: GoogleMap? = null
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var location: Location? = null
    private var locationManager: LocationManager? = null
    private val addMarkerAlertDialog: EditMarkerDialogFragment by lazy {
        EditMarkerDialogFragment(null, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun readArguments(bundle: Bundle) {
        markerId = bundle.getInt(MARKER_ID, -1)
        geoString = bundle.getString(GEO_STRING)
    }

    override fun initView() {
        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)
    }

    override fun initListeners() {
        binding.myLocationFab.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            getLocation()
        }
        binding.addMarkerFab.setOnClickListener {
            addMarkerAlertDialog.show(requireActivity().supportFragmentManager, "")
        }
        binding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    hideKeyboardAndSearch(v)
                    return@setOnEditorActionListener true
                }
                else -> return@setOnEditorActionListener false
            }
        }
        binding.searchEditLayout.setEndIconOnClickListener {
            hideKeyboardAndSearch(it.rootView)
        }
    }

    private fun hideKeyboardAndSearch(v: View) {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
        sendToGeocoderAndFind(binding.searchEdit.text.toString())
    }

    override fun initObservers() {
    }

    override fun onSaveClicked(title: String, information: String) {
        mapViewModel.saveMarker(
            MapPosition(
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0,
                title = title,
                information = information
            )
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_save_marker_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        if (markerId > 0) {
            mapViewModel.data.observe(viewLifecycleOwner) {
                it?.let {
                    map?.goToLocation(LatLng(it.latitude, it.longitude))
                    (activity as AppCompatActivity).supportActionBar?.title = it.title
                }
            }
            mapViewModel.getMarker(markerId)
            setMenuVisibility(false)
            markerId = -1
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled =
                locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            isNetworkEnabled =
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert()
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            (context as Activity?)!!,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), 101
                        )
                    }
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    )
                    location =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                (context as Activity?)!!, arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ), 101
                            )
                        }
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                        )
                        location =
                            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopUsingGPS() {
        locationManager?.removeUpdates(this)
    }

    private fun showSettingsAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.map_fragment_alert_dialog_title))
            .setMessage(getString(R.string.map_fragment_alert_dialog_message))
            .setPositiveButton(getString(R.string.map_fragment_alert_dialog_positive_button_text))
            { _, _ ->
                context?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.map_fragment_alert_dialog_negative_button_text))
            { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun GoogleMap.goToLocation(point: LatLng) {
        this.clear()
        this.addMarker(
            MarkerOptions()
                .position(point)
        )
        val cameraPosition = CameraPosition.Builder()
            .target(point)
            .zoom(MAP_ZOOM_ON_POSITION)
            .bearing(0f)
            .tilt(0f)
            .build()
        this.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        binding.addMarkerFab.isEnabled = true
    }

    private fun getLatLngFromGeoString(geoString: String): LatLng? {
        val locValues = Regex("-?\\d+.\\d+").findAll(geoString)
            .map { it.value.toDouble() }
            .toList()
        if (locValues.isNotEmpty()) {
            return LatLng(locValues[0], locValues[1])
        }
        return null
    }

    private fun sendToGeocoderAndFind(geoString: String) {
        thread {
            val geocoder = Geocoder(requireContext())
            try {
                var geoResults = geocoder.getFromLocationName(geoString, 1)
                while (geoResults.size == 0) {
                    geoResults = geocoder.getFromLocationName(geoString, 1)
                }
                val address = geoResults[0]
                location = Location("").apply {
                    latitude = address.latitude
                    longitude = address.longitude
                }
                map!!.goToLocation(LatLng(address.latitude, address.longitude))
            } catch (e: java.lang.Exception) {
                print(e.message)
            }
        }.run()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (map == null) {
            map = googleMap
            map?.setOnMapClickListener {
                location = Location("").apply {
                    latitude =
                        it.latitude.toBigDecimal().setScale(7, RoundingMode.FLOOR).toDouble()
                    longitude =
                        it.longitude.toBigDecimal().setScale(7, RoundingMode.FLOOR).toDouble()
                }
                map!!.goToLocation(it)
            }
        }
        geoString?.let { geoStr ->
            if (geoStr.substring(0, 3) == "geo") {
                getLatLngFromGeoString(geoStr)?.let { position ->
                    map?.goToLocation(position)
                }
            } else {
                sendToGeocoderAndFind(geoStr)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        binding.progressBar.visibility = View.GONE
        this.location = location
        map?.goToLocation(LatLng(location.latitude, location.longitude))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.app_bar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.markers -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MarkersFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        stopUsingGPS()
        super.onDestroyView()
    }

    companion object {
        fun newInstanceFromIntent(geoString: String?) = MapFragment().apply {
            geoString?.let { arguments = bundleOf(GEO_STRING to geoString) }
        }

        fun newInstance(markerId: Int?) = MapFragment().apply {
            markerId?.let { arguments = bundleOf(MARKER_ID to markerId) }
        }

        const val GEO_STRING = "geo_string"
        const val MARKER_ID = "marker_id"
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10.0F // 10 meters
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
        private const val MAP_ZOOM_ON_POSITION: Float = 18.0F
    }
}