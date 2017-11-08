package com.example.rodrigo.guedr.model

import com.example.rodrigo.guedr.R
import java.io.Serializable

object Cities: Serializable {
    private var cities: List<City> = listOf(
            City("Madrid"),
            City("Jaén"),
            City("Quito")
    )

    val count
        get() = cities.size

    operator fun get(i: Int) = cities[i]

    fun toArray() = cities.toTypedArray()
}

