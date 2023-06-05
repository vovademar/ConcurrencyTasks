package ru.nsu.medvedev.entities

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(val ticketNo: String, val bookingCode: String, val flightNo: String,
                   val setType: String, val price:String)
