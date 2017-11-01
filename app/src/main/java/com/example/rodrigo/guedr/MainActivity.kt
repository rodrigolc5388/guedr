package com.example.rodrigo.guedr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = MainActivity::class.java.canonicalName

    var offlineWeatherImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.stone_button).setOnClickListener(this)
        findViewById<Button>(R.id.donkey_button).setOnClickListener(this)

        offlineWeatherImage = findViewById(R.id.offline_weather_image)

        Log.v(TAG, "He pasado por onCreate")

        if (savedInstanceState != null) {
            Log.v(TAG,  "saedInstanceState no es null y clave vale: ${savedInstanceState.getString("clave")}")
        }
        else {
            Log.v(TAG, "savedInstanceState ES null")
        }
    }

    override fun onClick(v: View?) {
        Log.v(TAG, "Hemos pasado por onClick")


        // La forma más "Kotlin" de hacer esto
        Log.v(TAG, when (v?.id) {
            R.id.stone_button -> "Han pulsado el botón piedra"
            R.id.donkey_button -> "Han pulsado el botón burro"
            else -> "No sé qué me han pulsado"
        })

//        when (v?.id) {
//            R.id.stone_button -> offlineWeatherImage?.setImageResource(R.drawable.offline_weather)
//            R.id.donkey_button -> offlineWeatherImage?.setImageResource(R.drawable.offline_weather2)
//        }

        offlineWeatherImage?.setImageResource(when (v?.id) {
            R.id.donkey_button -> R.drawable.offline_weather2
            else -> R.drawable.offline_weather
        })


    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.v(TAG, "He pasado por onSaveInstanceState")

//        if (outState != null) {
//            outState.putString("clave", "valor")
//        }

        outState?.putString("clave", "valor")
    }
}
