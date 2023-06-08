package ru.nsu.medvedev.entities
import kotlinx.serialization.Serializable

@Serializable
data class Airport(val code: String, val name: String, val city: String, val coordinates: String, val timezone: String)
