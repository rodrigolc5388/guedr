package com.example.rodrigo.guedr.model

import com.example.rodrigo.guedr.R

class Cities {
    private var cities: List<City> = listOf(
            City("Madrid", Forecast(25f, 10f, 35f, "Soleado con alguna nubre", R.drawable.ico_02 )),
            City("Jaén", Forecast(36f, 19f, 19f, "Sol a tope", R.drawable.ico_01)),
            City("Quito", Forecast(30f, 15f, 40f, "Arcoiris", R.drawable.ico_10))
    )

    val count
        get() = cities.size

    operator fun get(i: Int) = cities[i]
}

