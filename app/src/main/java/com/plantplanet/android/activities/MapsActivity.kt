package com.plantplanet.android.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.plantplanet.android.R
import com.plantplanet.android.models.Alert
import com.plantplanet.android.utils.FirebaseHelper
import java.io.IOException

@SuppressLint("MissingPermission")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var country: String
    private lateinit var position: LatLng
    private val locationRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        checkPermissions()
        mMap = googleMap
        getLocation()
    }

    private fun getCountry() {
        val geocoder = Geocoder(this)
        var addresses: List<Address> = emptyList()
        try {
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1)
        } catch (ioException: IOException) {
            noGPS()
        }
        if (addresses.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.something_wrong), Toast.LENGTH_LONG).show()
        } else {
            country = addresses[0].countryCode
            addLocation()
            fetchDiseases()
        }
    }

    private fun fetchDiseases() {
        val db = FirebaseFirestore.getInstance()
        db.collection(country).get().addOnSuccessListener {
            val alerts = it.toObjects(Alert::class.java)
            for (alert in alerts) {
                val latLng = LatLng(alert.alertLatitude!!, alert.alertLongitude!!)
                mMap.addMarker(MarkerOptions().position(latLng).title(alert.alertDisease))
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun addLocation() {
        val disease = intent.getStringExtra("disease")
        if (disease != null) {
            val alert = Alert(position.latitude, position.longitude, disease)
            val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            FirebaseHelper().setDocument(country, id, alert)
                .addOnSuccessListener { Toast.makeText(applicationContext, getString(R.string.done_successfully), Toast.LENGTH_LONG).show() }
                .addOnFailureListener { Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show() }
        }
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                position = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
                getCountry()
            } else
                noGPS()
        }
    }

    private fun noGPS() {
        Toast.makeText(applicationContext, getString(R.string.gps_required), Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == locationRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
        else if (requestCode == locationRequestCode && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
            Toast.makeText(applicationContext, getString(R.string.location_required), Toast.LENGTH_LONG).show()
        }
    }
}
