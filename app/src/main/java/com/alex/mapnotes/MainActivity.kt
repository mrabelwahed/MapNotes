package com.alex.mapnotes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.button_sheet.*

const val LOCATION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(bottomSheet)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add_note -> {
                replaceBottomFragment(SaveNoteFragment())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search_notes -> {
                replaceBottomFragment(SearchNotesFragment())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun replaceBottomFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.bottomSheetContainer, fragment)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.selectedItemId = R.id.navigation_map
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted; show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionExplanationSnackBar()
                hideContentWhichRequirePermissions()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_REQUEST_CODE)
            }
        } else {
            showContentWhichRequirePermissions()
        }
    }

    private fun showPermissionExplanationSnackBar() {
        Snackbar
                .make(layout, R.string.permission_explanation, Snackbar.LENGTH_LONG)
                .setAction(R.string.permission_grant_text) {
                    ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE)
                }
                .show()
    }

    private fun showContentWhichRequirePermissions() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit()
        navigation.visibility = View.VISIBLE
        mapFragment.getMapAsync(this)
    }

    private fun hideContentWhichRequirePermissions() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.map, NoLocationPermissionFragment())
                .commit()
        navigation.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showContentWhichRequirePermissions()
                } else {
                    hideContentWhichRequirePermissions()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        val sydney = LatLng(-33.8688, 151.2093)
        map?.addMarker(MarkerOptions().position(sydney).title("Sydney"))
        map?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        map?.setMinZoomPreference(15.0f)
    }
}