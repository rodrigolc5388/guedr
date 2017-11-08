package com.example.rodrigo.guedr.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import com.example.rodrigo.guedr.CONSTANT_OWM_APIKEY
import com.example.rodrigo.guedr.PREFERENCE_SHOW_CELSIUS
import com.example.rodrigo.guedr.R
import com.example.rodrigo.guedr.activity.SettingsActivity
import com.example.rodrigo.guedr.model.City
import com.example.rodrigo.guedr.model.Forecast
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ForecastFragment: Fragment() {

    enum class VIEW_INDEX(val index: Int) {
        LOADING(0),
        FORECAST(1)
    }

    companion object {
        val REQUEST_UNITS = 1
        private val ARG_CITY = "ARG_CITY"

        fun newInstance(city: City): ForecastFragment{
            val fragment = ForecastFragment()

            val arguments = Bundle()
            arguments.putSerializable(ARG_CITY, city)
            fragment.arguments = arguments

            return fragment
        }
    }

    lateinit var root: View
    lateinit var maxTemp: TextView
    lateinit var minTemp: TextView
    lateinit var viewSwitcher: ViewSwitcher

    var city: City? = null
        set(value) {
            field = value
            if( value != null){
                root.findViewById<TextView>(R.id.city).setText(value.name)
                forecast = value.forecast
            }
        }

    var forecast: Forecast? = null
        set(value) {
            field = value
            // Accedemos a las vistas de la interfaz
            val forecastImage = root.findViewById<ImageView>(R.id.forecast_image)
            maxTemp = root.findViewById(R.id.max_temp)
            minTemp = root.findViewById(R.id.min_temp)
            val humidity = root.findViewById<TextView>(R.id.humidity)
            val forecastDescription = root.findViewById<TextView>(R.id.forecast_description)

            // Actualizamos la vista con el modelo
            if (value != null) {
                forecastImage.setImageResource(value.icon)
                forecastDescription.setText(value.description)
                updateTemperature()
                val humidityString = getString(R.string.humidity_format, value.humidity)
                humidity.text = humidityString
                viewSwitcher.displayedChild = VIEW_INDEX.FORECAST.index
            }
            else {
                updateForecast()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        if (inflater != null) {
            root = inflater.inflate(R.layout.fragment_forecast, container, false)

            viewSwitcher = root.findViewById(R.id.view_switcher)
            viewSwitcher.setInAnimation(activity, android.R.anim.fade_in)
            viewSwitcher.setOutAnimation(activity, android.R.anim.fade_out)

            if (arguments != null) {
                city = arguments.getSerializable(ARG_CITY) as? City
            }
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_show_settings) {
            // Aquí sabemos que se ha pulsado la opción de menú de mostrar ajustes
            val intent = SettingsActivity.intent(activity, if(temperatureUnits() == Forecast.TempUnit.CELSIUS) R.id.celsius_rb
            else R.id.farenheit_rb)
            // Esto lo haríamos si la segunda pantalla no nos tiene que devolver nada
            //startActivity(intent)

            // Esto lo hacemos porque la segunda pantalla nos tiene que devolver unos valores
            startActivityForResult(intent, REQUEST_UNITS)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNITS) {
            if (resultCode == Activity.RESULT_OK){

                val unitSelected = data?.getIntExtra(SettingsActivity.EXTRA_UNITS, R.id.celsius_rb)

                when (unitSelected){
                    R.id.celsius_rb -> {
                        Log.v("TAG", "Soy ForecastActivity y han pulsado OK y Celsius")
                        // Toast.makeText(this, "Celsius seleccionado", Toast.LENGTH_LONG).show()

                    }
                    R.id.farenheit_rb -> {
                        Log.v("TAG", "Soy ForecastActivity y han pulsado OK y Fahrenheit")
                        // Toast.makeText(this, "Fahrenheit seleccionado", Toast.LENGTH_LONG).show()
                    }
                }

                val oldShowCelsius = temperatureUnits()

                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit()
                        .putBoolean(PREFERENCE_SHOW_CELSIUS, unitSelected == R.id.celsius_rb)
                        .apply()

                updateTemperature()

                Snackbar.make(root, "Han cambiado las preferencias", Snackbar.LENGTH_LONG)
                        .setAction("Deshacer"){
                            PreferenceManager.getDefaultSharedPreferences(activity)
                                    .edit()
                                    .putBoolean(PREFERENCE_SHOW_CELSIUS, oldShowCelsius == Forecast.TempUnit.CELSIUS)
                                    .apply()
                            updateTemperature()

                        }
                        .show()

            } else {
                Log.v ("TAG", "Soy ForecastActivity y han pulsado CANCEL")
            }
        }
    }

    // Para saber si, estando en un ViewPager por ejemplo, debemos refrescar las unidades de las temperaturas
    // Es algo así como el viewWillAppear de los Fragment
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && forecast != null) {
            updateTemperature()
        }
    }

    private fun updateForecast() {
        viewSwitcher.displayedChild = VIEW_INDEX.LOADING.index
       async(UI) {
           val newForecast: Deferred<Forecast?> = bg {
               downloadForecast(city)
           }

           forecast = newForecast.await()
       }
    }

    fun downloadForecast(city: City?): Forecast? {
        try {
            // Nos descargamos la información del tiempo a machete
            val url = URL("https://api.openweathermap.org/data/2.5/forecast/daily?q=${city?.name}&lang=sp&units=metric&appid=${CONSTANT_OWM_APIKEY}")
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            // Analizamos los datos que nos acabamos de descargar
            val jsonRoot = JSONObject(jsonString)
            val list = jsonRoot.getJSONArray("list")
            val today = list.getJSONObject(0)
            val max = today.getJSONObject("temp").getDouble("max").toFloat()
            val min = today.getJSONObject("temp").getDouble("min").toFloat()
            val humidity = today.getDouble("humidity").toFloat()
            val description = today.getJSONArray("weather").getJSONObject(0).getString("description")
            var iconString = today.getJSONArray("weather").getJSONObject(0).getString("icon")

            // Convertimos el texto iconString a un drawable
            iconString = iconString.substring(0, iconString.length - 1)
            val iconInt = iconString.toInt()
            val iconResource = when (iconInt) {
                2 -> R.drawable.ico_02
                3 -> R.drawable.ico_03
                4 -> R.drawable.ico_04
                9 -> R.drawable.ico_09
                10 -> R.drawable.ico_10
                11 -> R.drawable.ico_11
                13 -> R.drawable.ico_13
                50 -> R.drawable.ico_50
                else -> R.drawable.ico_01
            }

            // Thread.sleep(5000)

            return Forecast(max, min, humidity, description, iconResource)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    private fun updateTemperature() {
        val units = temperatureUnits()
        val unitsString = temperatureUnitsString(units)
        val maxTempString = getString(R.string.max_temp_format, forecast?.getMaxTemp(units), unitsString)
        val minTempString = getString(R.string.min_temp_format, forecast?.getMinTemp(units), unitsString)
        maxTemp.text = maxTempString
        minTemp.text = minTempString


    }

    private fun temperatureUnitsString(units: Forecast.TempUnit) = when (units) {
        Forecast.TempUnit.CELSIUS -> "ºC"
        else -> "F"
    }

    private fun temperatureUnits(): Forecast.TempUnit = if (PreferenceManager.getDefaultSharedPreferences(activity)
            .getBoolean(PREFERENCE_SHOW_CELSIUS, true)) {
        Forecast.TempUnit.CELSIUS
    } else {
        Forecast.TempUnit.FAHRENHEIT
    }
}