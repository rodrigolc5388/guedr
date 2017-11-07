package com.example.rodrigo.guedr.model

import java.io.Serializable

data class Forecast(val maxTemp: Float, val minTemp: Float, val humidity: Float, val description: String, val icon: Int): Serializable{
    enum class TempUnit{
        CELSIUS,
        FAHRENHEIT
    }

    protected fun toFahrenheit(celsius: Float) = celsius * 1.8 + 32

    init {
        if (humidity !in 0f..100f) {
            throw IllegalArgumentException("Humidity should be between 0 and 100")
        }
    }

    fun getMaxTemp(units: TempUnit) = when(units) {
        TempUnit.CELSIUS -> maxTemp
        TempUnit.FAHRENHEIT -> toFahrenheit(maxTemp)
    }

    fun getMinTemp(units: TempUnit) = when(units) {
        TempUnit.CELSIUS -> minTemp
        TempUnit.FAHRENHEIT -> toFahrenheit(minTemp)
    }
}