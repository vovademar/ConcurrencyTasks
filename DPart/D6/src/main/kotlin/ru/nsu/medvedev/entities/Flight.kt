package ru.nsu.medvedev.entities

import kotlinx.serialization.Serializable

@Serializable
data class Flight(
    val flightNo: String, val departureAirport: String, val departureCity: String,
    val arrivalAirport: String, val arrivalCity: String, val days: String
)
