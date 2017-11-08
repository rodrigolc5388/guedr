package com.example.rodrigo.guedr.adapter

import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.rodrigo.guedr.R
import com.example.rodrigo.guedr.model.Forecast
import kotlinx.android.synthetic.main.content_forecast.view.*


class ForecastRecyclerViewAdapter(val forecast: List<Forecast>, val tempUnit: Forecast.TempUnit) : RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.content_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder?, position: Int) {
        holder?.bindForecast(forecast[position], tempUnit, position)
    }

    override fun getItemCount() = forecast.size


    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val day = itemView.findViewById<TextView>(R.id.day)
        val forecastImage = itemView.findViewById<ImageView>(R.id.forecast_image)
        val maxTemp = itemView.findViewById<TextView>(R.id.max_temp)
        val minTemp = itemView.findViewById<TextView>(R.id.min_temp)
        val humidity = itemView.findViewById<TextView>(R.id.humidity)
        val forecastDescription = itemView.findViewById<TextView>(R.id.forecast_description)

        fun bindForecast(forecast: Forecast, tempUnit: Forecast.TempUnit, position: Int) {
            // Accedemos al contexto
            val context = forecastImage.context

            // Actualizamos la vista con el modelo
            forecastImage.setImageResource(forecast.icon)
            forecastDescription.text = forecast.description
            updateTemperature(forecast, tempUnit)
            val humidityString = context.getString(R.string.humidity_format, forecast.humidity)
            humidity.text = humidityString
            day.text = generateDayText(position)
        }

        private fun generateDayText(position: Int) = when(position) {
            0 -> "Hoy"
            1 -> "Mañana"
            2 -> "Pasado mañana"
            3 -> "Pasado pasado mañana"
            4 -> "Pasado pasado pasado mañana"
            5 -> "Pasado pasado pasado pasado mañana"
            else -> "Pues yo qué sé tío!"
        }

        private fun updateTemperature(forecast: Forecast, units: Forecast.TempUnit) {
            val unitsString = temperatureUnitsString(units)
            val maxTempString = itemView.context.getString(R.string.max_temp_format, forecast?.getMaxTemp(units), unitsString)
            val minTempString = itemView.context.getString(R.string.min_temp_format, forecast?.getMinTemp(units), unitsString)
            maxTemp.text = maxTempString
            minTemp.text = minTempString
        }

        private fun temperatureUnitsString(units: Forecast.TempUnit) = when (units) {
            Forecast.TempUnit.CELSIUS -> "ºC"
            else -> "F"
        }
    }

}