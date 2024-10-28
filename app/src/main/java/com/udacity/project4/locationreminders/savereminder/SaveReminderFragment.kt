package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.EspresoIdlingResources
import com.udacity.project4.utils.LocationHelper
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    private lateinit var client: GeofencingClient


    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel


        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setUp()


    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(reminder: ReminderDataItem) {
        EspresoIdlingResources.wrapEspressoIdlingResource {
            val geofence = getGEofence(reminder)
            val request = GeofencingRequest.Builder().addGeofence(geofence).build()
            val sendBraodcast = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
            val intent = PendingIntent.getBroadcast(
                requireContext(), 0, sendBraodcast, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val task = client.addGeofences(request, intent)
            task.addOnSuccessListener {
                Toast.makeText(requireContext(), "Geofence Added", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun getGEofence(reminder: ReminderDataItem): Geofence {
        return Geofence.Builder().setCircularRegion(
            reminder.latitude ?: 0.0, reminder.longitude ?: 0.0, 80f
        ).setRequestId(reminder.id).setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build()

    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    fun saveReminder(reminder: ReminderDataItem) {

        binding.progressBar.visibility = View.VISIBLE
        _viewModel.saveReminder(reminder)
        binding.progressBar.visibility = View.GONE
    }


    fun save() {
        LocationHelper.checkLocationAnd(
            requireContext(),
            {
                var reminder: ReminderDataItem
                with(_viewModel) {
                    reminder = ReminderDataItem(
                        reminderTitle.value,
                        reminderDescription.value,
                        reminderSelectedLocationStr.value,
                        latitude.value,
                        longitude.value
                    )
                }
                if (_viewModel.validateEnteredData(reminder)) {
                    saveReminder(reminder)
                    addGeofence(reminder)
                }
            },
            {
                requestLocation(it)
            }
        )
    }

    fun setUp() {

        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }
        client = LocationServices.getGeofencingClient(requireContext())

        binding.saveReminder.setOnClickListener {

            if (LocationHelper.checkPermissions(requireContext())) {
                save()
            } else
                requestPermissions()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (LocationHelper.checkPermissions(requireContext()))
            save()
        else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }


    }

    fun requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LocationHelper.PERMMISION_REQUEST_CODE
            )
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LocationHelper.PERMMISION_REQUEST_CODE
            )

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationHelper.REQUEST_ENABLE_LOCATION)
            if (resultCode == Activity.RESULT_OK) {
                save()
            }
    }


    fun requestLocation(it: ResolvableApiException) {
        startIntentSenderForResult(
            it.resolution.intentSender,
            LocationHelper.REQUEST_ENABLE_LOCATION, null, 0, 0, 0, null
        )


    }
}
