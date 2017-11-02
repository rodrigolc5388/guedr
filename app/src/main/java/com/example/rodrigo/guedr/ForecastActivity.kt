package com.example.rodrigo.guedr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView

class ForecastActivity : AppCompatActivity() {

    var forecast: Forecast? = null
        set(value) {
                // Accedemos a las vistas de la interfaz
                val forecastImage = findViewById<ImageView>(R.id.forecast_image)
                val maxTemp = findViewById<TextView>(R.id.max_temp)
                val minTemp = findViewById<TextView>(R.id.min_temp)
                val humidity = findViewById<TextView>(R.id.humidity)
                val forecastDescription = findViewById<TextView>(R.id.forecast_description)

                // Actualizamos la vista con el modelo
                if (value != null) {
                    forecastImage.setImageResource(value.icon)
                    forecastDescription.setText(value.description)
                    val maxTempString = getString(R.string.max_temp_format, value.maxTemp)
                    val minTempString = getString(R.string.min_temp_format, value.minTemp)
                    val humidityString = getString(R.string.humidity_format, value.humidity)
                    maxTemp.text = maxTempString
                    minTemp.text = minTempString
                    humidity.text = humidityString
                }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        forecast = Forecast(25f, 10f, 35f, "Soleado con alguna nube", R.drawable.ico_01)
    }


    // Este método define qué opciones de menú tenemos
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.settings, menu)

        return true
    }


    // Este método dice qué se hace una vez que se ha pulsado una opción de menú
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_show_settings) {
            // Aquí sabemos que se ha pulsado la opción de menú de mostrar ajustes
            val intent = Intent(this, SettingsActivity::class.java)
            // Esto lo haríamos si la segunda pantalla no nos tiene que devolver nada
            //startActivity(intent)

            // Esto lo hacemos porque la segunda pantalla nos tiene que devolver unos valores
            startActivityForResult(intent, 1)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                Log.v ("TAG", "Soy ForecastActivity y han pulsado OK")
            } else {
                Log.v ("TAG", "Soy ForecastActivity y han pulsado CANCEL")
            }
        }
    }


}
