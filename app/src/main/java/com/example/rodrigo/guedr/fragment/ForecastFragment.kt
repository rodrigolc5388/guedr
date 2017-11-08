package com.example.rodrigo.guedr.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import com.example.rodrigo.guedr.CONSTANT_OWM_APIKEY
import com.example.rodrigo.guedr.PREFERENCE_SHOW_CELSIUS
import com.example.rodrigo.guedr.R
import com.example.rodrigo.guedr.activity.SettingsActivity
import com.example.rodrigo.guedr.adapter.ForecastRecyclerViewAdapter
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
    lateinit var viewSwitcher: ViewSwitcher
    lateinit var forecastList: RecyclerView

    var city: City? = null
        set(value) {
            field = value
            if( value != null){
                forecast = value.forecast
            }
        }

    var forecast: List<Forecast>? = null
        set(value) {
            field = value
            // Actualizamos la vista con el modelo
            if (value != null) {
                // Asignamos el adapter al RecyclerView ahora que tenemos datos
                forecastList.adapter = ForecastRecyclerViewAdapter(value, temperatureUnits())

                viewSwitcher.displayedChild = VIEW_INDEX.FORECAST.index
                city?.forecast = value // Supercaché de la "muerte"
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

            // 1) Accedemos al RecyclerView con findViewById
            forecastList = root.findViewById(R.id.forecast_list)

            // 2) Le decimos cómo debe visualizarse el RecyclerView (su LayoutManager)
            forecastList.layoutManager = LinearLayoutManager(activity)

            // 3) Le decimos cómo debe animarse el RecyclerView (su ItemAnimator)
            forecastList.itemAnimator = DefaultItemAnimator()

            // 4) Por último, un RecyclerView necesita un adapter
            // Esto aún no lo hacemos aquí porque aquí aún no tenemos datos


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
           val newForecast: Deferred<List<Forecast>?> = bg {
               downloadForecast(city)
           }

           val downloadedForecast = newForecast.await()
           if (downloadedForecast != null) {
               // Tóo ha ido bien, se lo asigno al atributo forecast
               forecast = downloadedForecast
           }
           else {
               // Ha habido algún tipo de error, se lo decimos al usuario con un código
               AlertDialog.Builder(activity)
                       .setTitle("Error")
                       .setMessage("No me pude descargar la información del tiempo")
                       .setPositiveButton("Reintentar", {dialog, _ ->
                           dialog.dismiss()
                           updateForecast() })
                       .setNegativeButton("Salir", { _, _ -> activity.finish() })
                       .show()
           }
       }
    }

    fun downloadForecast(city: City?): List<Forecast>? {
        try {
            // Nos descargamos la información del tiempo a machete
            val url = URL("https://api.openweathermap.org/data/2.5/forecast/daily?q=${city?.name}&lang=sp&units=metric&appid=${CONSTANT_OWM_APIKEY}")
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            // Analizamos los datos que nos acabamos de descargar
            val jsonRoot = JSONObject(jsonString)
            val list = jsonRoot.getJSONArray("list")

            // Nos creamos la lista que vamos a ir rellenando con las predicciones del JSON
            var forecasts = mutableListOf<Forecast>()

            // Recorremos la lista del objeto JSON
            for (dayIndex in 0..list.length() -1) {
                val today = list.getJSONObject(dayIndex)
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

                forecasts.add(Forecast(max, min, humidity, description, iconResource))
            }

            return forecasts
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    private fun updateTemperature() {
        forecastList.adapter = ForecastRecyclerViewAdapter(forecast, temperatureUnits())
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