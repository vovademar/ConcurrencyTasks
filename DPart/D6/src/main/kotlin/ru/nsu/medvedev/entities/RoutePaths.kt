package ru.nsu.medvedev.entities

import kotlinx.serialization.Serializable

@Serializable
data class RoutePaths(val arrivalAirport: String, val path: String, val seatType: String,
                      val departureDate: String, val arrivalDate: String)
