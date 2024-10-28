package com.udacity.project4.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object LocationHelper {
    val REQUEST_ENABLE_LOCATION: Int=3213213
    val PERMMISION_REQUEST_CODE: Int = 48762783

    @RequiresApi(Build.VERSION_CODES.Q)
    fun permissionsGrantedBackGround(context: Context): Boolean {

        return ActivityCompat.checkSelfPermission(
          context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED



    }

    fun permissionsGrantedFourGround(context: Context): Boolean {

        return (ActivityCompat.checkSelfPermission(
           context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    fun  checkPermissions(context: Context):Boolean{
        var granted: Boolean = permissionsGrantedFourGround(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            granted = permissionsGrantedBackGround(context) && granted
        }
        return granted
    }


    fun  checkLocationAnd(context: Context, function:()->Unit,requestLocation : (ResolvableApiException)->Unit ){
        val locationRequest = LocationRequest.create().apply {
            priority= LocationRequest.PRIORITY_LOW_POWER
        }
        val locationSRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(context)
            .checkLocationSettings(locationSRequest.build())
        task.addOnCompleteListener{
            if(  it.isSuccessful)
            {
                function()
            }
            else if (it.isCanceled)
            {
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show()
            }

            else if (!it.isSuccessful)
            {
                if (it.exception is ResolvableApiException )
                {
                    requestLocation(it.exception as ResolvableApiException)
                }
                else
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


}