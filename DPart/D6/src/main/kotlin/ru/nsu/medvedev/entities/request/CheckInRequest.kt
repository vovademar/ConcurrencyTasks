package ru.nsu.medvedev.entities.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckInRequest(val ticketNo: String)