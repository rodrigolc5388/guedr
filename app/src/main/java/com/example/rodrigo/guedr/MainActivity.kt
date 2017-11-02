package com.example.rodrigo.guedr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val forecast = Forecast(25f, 10f, 35f, "Soleado con alguna nube", R.drawable.ico_01)

        setForecast(forecast)

    }

    private fun setForecast(forecast: Forecast) {

    }

}
