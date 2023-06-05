package ru.nsu.medvedev.entities

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequest(
    val flightNo: String,
    val seatType: String,
    val date: String,
    val name: String,
    val passengerID: String,
    val phone: String
)
