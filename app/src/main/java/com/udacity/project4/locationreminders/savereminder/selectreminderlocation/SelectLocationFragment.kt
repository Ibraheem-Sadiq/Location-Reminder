package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.EspresoIdlingResources
import com.udacity.project4.utils.LocationHelper
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {
    private val PERMMISION_REQUEST_CODE_FOREGROUND: Int = 48762783
    var map: GoogleMap? = null
    var mLatLng: LatLng = LatLng(1.00, 1.00)
    var mTitleOfSelectedLocation = "title"
    var mSelectedPoi: PointOfInterest? = null

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        binding.floatingActionButton.setOnClickListener { onLocationSelected() }
        return binding.root
    }


    private fun onLocationSelected() {
        EspresoIdlingResources.wrapEspressoIdlingResource {
            with(_viewModel) {
                longitude.value = mLatLng.longitude
                latitude.value = mLatLng.latitude
                selectedPOI.value = mSelectedPoi
                reminderSelectedLocationStr.value = mTitleOfSelectedLocation
                navigationCommand.value = NavigationCommand.Back
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL

        map?.setOnMapLongClickListener {
            mapLongClicked(it)
        }
        map?.setOnPoiClickListener {
            mapClicked(it)
        }


        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))

        if (LocationHelper.permissionsGrantedFourGround(requireContext())) {
            enableMyLocationButton()
            LocationHelper.checkLocationAnd(requireContext(), {
                locateDevice(20f)
            }, { enableLocation(it) })
        } else
            requestPermissionsForeGround()


    }

    fun mapLongClicked(it: LatLng) {
        map?.clear()
        mLatLng = it
        mTitleOfSelectedLocation = "${mLatLng.latitude}  & ${mLatLng.longitude }"
        mSelectedPoi = PointOfInterest(mLatLng, mTitleOfSelectedLocation, mTitleOfSelectedLocation)
        drawCircle(80.0)
    }

    fun mapClicked(it: PointOfInterest) {
        map?.clear()
        mSelectedPoi = it
        mLatLng = it.latLng
        mTitleOfSelectedLocation = it.name
        drawCircle(80.0)
    }


    fun drawCircle(r: Double) {
        map!!.addMarker(MarkerOptions().title(mTitleOfSelectedLocation).position(mLatLng))
        map!!.addCircle(CircleOptions().center(mLatLng).radius(r))
    }


    @SuppressLint("MissingPermission")
    fun locateDevice(zoom: Float) {
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnCompleteListener {
            if (it.isSuccessful)
                if (it.result != null) map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.result.latitude, it.result.longitude), zoom
                    )
                )
                else Toast.makeText(
                    requireContext(), "Error : cannot detect Location ", Toast.LENGTH_SHORT
                ).show()
        }

    }

    fun requestPermissionsForeGround() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMMISION_REQUEST_CODE_FOREGROUND
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (LocationHelper.permissionsGrantedFourGround(requireContext())) {
            enableMyLocationButton()
            LocationHelper.checkLocationAnd(requireContext(), {
                locateDevice(20f)
            }, { enableLocation(it) })
        } else
            Toast.makeText(
                requireContext(), "Error : cannot detect Location ", Toast.LENGTH_SHORT
            ).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //this because i have found a delay in enabling Location wich
        if (requestCode == LocationHelper.REQUEST_ENABLE_LOCATION)
            if (resultCode == Activity.RESULT_OK) {
             locateDevice(20f)
            }
    }

    fun enableLocation(it: ResolvableApiException) {
        startIntentSenderForResult(
            it.resolution.intentSender,
            LocationHelper.REQUEST_ENABLE_LOCATION, null, 0, 0, 0, null
        )
    }

    fun enableMyLocationButton(){
        map?.setMyLocationEnabled(true)
        map?.uiSettings?.isMyLocationButtonEnabled =true
        map?.setOnMyLocationButtonClickListener(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
            LocationHelper.checkLocationAnd(requireContext(), {
                locateDevice(20f)
            }, { enableLocation(it) })

        return true
    }


}
