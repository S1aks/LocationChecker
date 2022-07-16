package com.s1aks.locchecker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.s1aks.locchecker.R
import com.s1aks.locchecker.databinding.ActivityMainBinding
import com.s1aks.locchecker.ui.map.MapFragment


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val geoString = when {
            intent?.action == Intent.ACTION_VIEW && intent.type?.startsWith("geo") == true -> {
                intent.getStringExtra(android.location.LocationManager.KEY_LOCATION_CHANGED)
            }
            intent?.action == Intent.ACTION_SEND && intent.type == "text/plain" -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            }
            else -> null
        }
        if (savedInstanceState == null) {
            if (geoString != null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, MapFragment.newInstanceFromIntent(geoString))
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, MapFragment.newInstance(null))
                    .commit()
            }
        }
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}