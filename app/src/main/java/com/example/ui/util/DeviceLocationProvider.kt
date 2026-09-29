package com.example.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.example.data.model.CityLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

object DeviceLocationProvider {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentDeviceLocation(context: Context): Result<CityLocation> = withContext(Dispatchers.IO) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()

            var location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token
            ).await()

            if (location == null) {
                location = fusedLocationClient.lastLocation.await()
            }

            if (location == null) {
                return@withContext Result.failure(Exception("GPS location unavailable. Please ensure Location is enabled in device settings."))
            }

            val lat = location.latitude
            val lng = location.longitude

            // Timezone offset in hours (IST is 5.5)
            val tz = TimeZone.getDefault()
            val offsetHours = tz.getOffset(System.currentTimeMillis()).toDouble() / (1000.0 * 60.0 * 60.0)

            var cityName = "Device GPS Location"
            var countryName = "India"

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                    cityName = locality ?: "Current Location"
                    countryName = addr.countryName ?: "India"
                }
            } catch (_: Exception) {
                cityName = "GPS (${String.format(Locale.US, "%.2f", lat)}, ${String.format(Locale.US, "%.2f", lng)})"
            }

            val cityLocation = CityLocation(
                name = cityName,
                country = countryName,
                latitude = lat,
                longitude = lng,
                timezoneOffsetHours = offsetHours
            )

            Result.success(cityLocation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
