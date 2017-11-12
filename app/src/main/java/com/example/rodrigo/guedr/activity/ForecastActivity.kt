package com.example.rodrigo.guedr.activity

import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.rodrigo.guedr.BuildConfig
import com.example.rodrigo.guedr.R
import com.example.rodrigo.guedr.fragment.CityListFragment
import com.example.rodrigo.guedr.fragment.CityPagerFragment
import com.example.rodrigo.guedr.model.City

class ForecastActivity : AppCompatActivity(), CityListFragment.OnCitySelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Chuleta para saber los detalles físicos del dispositivo donde se ejecuta esto
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val dpWith = (width / metrics.density).toInt()
        val dpHeight = (height / metrics.density).toInt()
        val dpi = metrics.densityDpi
        val model = Build.MODEL
        val androidVersion = Build.VERSION.SDK_INT


        // Comprobamos que en la interfaz tenemos un FrameLayout llamado city_list_fragment
        if (findViewById<View>(R.id.city_list_fragment) != null) {
            // Comprobamos primero que no tenemos ya añadido el fragment a nuestra jerarquía
            if (fragmentManager.findFragmentById(R.id.city_list_fragment) == null) {
                val fragment = CityListFragment.newInstance()
                fragmentManager.beginTransaction()
                        .add(R.id.city_list_fragment, fragment)
                        .commit()
            }
        }

        // Hacemos lo mismo pero con el fragment de CityPagerFragment
        if (findViewById<View>(R.id.fragment_city_pager) != null ) {
            if (fragmentManager.findFragmentById(R.id.fragment_city_pager ) == null) {
                val fragment = CityPagerFragment.newInstance(0)
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_city_pager, fragment)
                        .commit()
            }
        }

        findViewById<FloatingActionButton>(R.id.add_city_button)?.setOnClickListener { v: View ->
            Snackbar.make(
                    v,
                    "Aquí haríamos cosas interesantes",
                    Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    override fun onCitySelected(city: City?, position: Int) {
        val cityPagerFragment = fragmentManager.findFragmentById(R.id.fragment_city_pager) as? CityPagerFragment
        if (cityPagerFragment == null) {
            startActivity(CityPagerActivity.intent(this, position))
        }
        else {
            cityPagerFragment.moveToCity(position)
        }
    }
}
