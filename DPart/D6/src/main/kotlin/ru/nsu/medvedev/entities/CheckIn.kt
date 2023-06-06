package ru.nsu.medvedev.entities

import kotlinx.serialization.Serializable

@Serializable
data class CheckIn(val seat: String, val boardingNo: String, val flightNo: String)
