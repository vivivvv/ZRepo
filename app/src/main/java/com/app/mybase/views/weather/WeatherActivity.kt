package com.app.mybase.views.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.BuildConfig
import com.app.mybase.R
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityWeatherBinding
import com.app.mybase.helper.ApisResponse
import com.app.mybase.helper.Utils
import com.app.mybase.helper.gone
import com.app.mybase.helper.visible
import com.app.mybase.model.WeatherDataModel
import com.app.mybase.views.weather.adaptor.ForecastListAdapter
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherActivity : BaseActivity<ActivityWeatherBinding, WeatherViewModel>() {

    val TAG = this::class.java.name

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private val gpsStatusReceiver = GpsStatusReceiver()
    private var isReceiverRegistered = false
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ForecastListAdapter
    var forecastWeatherList = ArrayList<WeatherDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]
        binding.weatherViewModel = viewModel
        binding.lifecycleOwner = this@WeatherActivity

        // Initially hide the UI values and show after API data received
        viewModel.showProgress()
        initializeForecastRV()

        // Initialize values
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get Location information
        checkLocationPermission()

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showDialog(
                    "Location Permission Needed",
                    "This app needs the Location permission, please accept to use location functionality"
                )
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsStatusMap ->
        if (!permissionsStatusMap.containsValue(false)) {
            // Get location information
            getLocationData()
        } else {
            showDialog(
                "Location Permission Needed",
                "This app needs the Location permission, please accept to use location functionality"
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationData() {
        if (isLocationEnabled()) {
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    doAPICall(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        BuildConfig.weather_api_key
                    )
                }
            }
        } else {
            showToast("Turn on location", Toast.LENGTH_LONG)
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun getWeather(lat: String, lon: String, appid: String) {
        viewModel.getWeather(lat, lon, appid).observe(this, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    Log.d(TAG, "getWeather: ${apiResponse.response}")
                    setWeatherData(apiResponse.response)
                }
                is ApisResponse.Error -> {
                    showToast(apiResponse.exception, Toast.LENGTH_SHORT)
                }
                is ApisResponse.Loading -> {
                    viewModel.showProgress()
                }
                is ApisResponse.Complete -> {
                    viewModel.hideProgress()
                }
                else -> {}
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData(weatherDataModel: WeatherDataModel) {
        binding.apply {
            dateTimeText.text = Utils.convertTimestampToDate(weatherDataModel.dt)
            setAnimImage(this, weatherDataModel.weather[0].main)
            statusText.text = weatherDataModel.weather[0].main
            degreeText.text = weatherDataModel.main.temp.roundToInt().toString()
            minTemp.text = "Min: ${String.format("%.4f", weatherDataModel.main.temp_min)}"
            maxTemp.text = "Max: ${String.format("%.4f", weatherDataModel.main.temp_max)}"
            cityText.text = weatherDataModel.name
            windValue.text = weatherDataModel.wind.speed.toString()
            humidityValue.text = weatherDataModel.main.humidity.toString()
            seaValue.text = weatherDataModel.main.sea_level.toString()
        }
    }

    private fun setAnimImage(binding: ActivityWeatherBinding, main: String) {
        when (main) {
            "Thunderstorm", "Drizzle", "Rain" -> {
                binding.animationView.setAnimation(R.raw.rain)
            }
            "Snow" -> {
                binding.animationView.setAnimation(R.raw.snow)
            }
            "Clouds" -> {
                binding.animationView.setAnimation(R.raw.clouds)
            }
            "Clear" -> {
                binding.animationView.setAnimation(R.raw.sun)
            }
            else -> {
                binding.animationView.setAnimation(R.raw.clouds)
            }
        }
        binding.animationView.playAnimation()
    }

    private fun getForecastWeather(lat: String, lon: String, appid: String) {
        viewModel.getForecastWeather(lat, lon, appid).observe(this, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    forecastWeatherList.clear()
                    forecastWeatherList.addAll(
                        apiResponse.response.list.subList(
                            2,
                            apiResponse.response.list.lastIndex
                        )
                    )
                    adapter.updateForecastList(forecastWeatherList)
                }
                is ApisResponse.Error -> {
                    showToast(apiResponse.exception, Toast.LENGTH_SHORT)
                }
                is ApisResponse.Loading -> {
                    viewModel.showProgress()
                }
                is ApisResponse.Complete -> {
                    viewModel.hideProgress()
                }
                else -> {}
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Register GPS receiver
        registerGPSReceiver()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGpsStatus()
        }
    }

    private fun registerGPSReceiver() {
        if (!isReceiverRegistered) {
            // Register a BroadcastReceiver to listen for GPS status changes
            val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            registerReceiver(gpsStatusReceiver, filter)
            isReceiverRegistered = true
        }
    }

    private fun checkGpsStatus() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            binding.infoText.gone()
            binding.weatherDetailsLayout.visible()
            requestNewLocationData()
        } else {
            viewModel.hideProgress()
            binding.infoText.visible()
            binding.weatherDetailsLayout.gone()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(0)
            .setMaxUpdateDelayMillis(0)
            .setMaxUpdates(1)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location? = locationResult.lastLocation
            if (location == null) {
                requestNewLocationData()
            } else {
                doAPICall(
                    location.latitude.toString(),
                    location.longitude.toString(),
                    BuildConfig.weather_api_key
                )
            }
        }
    }

    fun doAPICall(latitude: String, longitude: String, apiKey: String) {
        // For today details
        getWeather(latitude, longitude, apiKey)
        // For forecast details
        getForecastWeather(latitude, longitude, apiKey)
    }

    // Receiver for GPS connection changes
    inner class GpsStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                checkGpsStatus()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (isReceiverRegistered) {
            // Unregister the BroadcastReceiver when the activity goes into the background
            unregisterReceiver(gpsStatusReceiver)
            isReceiverRegistered = false
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun initializeForecastRV() {
        recyclerView = binding.forecastRv
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = ForecastListAdapter(this@WeatherActivity)
        recyclerView.adapter = adapter
    }

    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                getString(R.string.ok)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    data = uri
                    startActivity(this)
                }
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

}