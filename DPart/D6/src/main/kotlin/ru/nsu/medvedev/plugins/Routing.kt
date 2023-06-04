package ru.nsu.medvedev.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.nsu.medvedev.database.DAO
import ru.nsu.medvedev.entities.Airport
import ru.nsu.medvedev.entities.City
import ru.nsu.medvedev.entities.Flight

fun Application.configureRouting() {
    val dao = DAO()
    dao.connectToDB()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
//        airportRouting()
        get("/cities") {
            val cities = dao.getCities()
            if (cities.isEmpty()) {
                call.respond(HttpStatusCode.OK, "No cities found")
            }
            val citiesResponse = ArrayList<City>()
            cities.forEach {
                citiesResponse.add(City(it.city))
            }
            call.respond(HttpStatusCode.OK, citiesResponse)
        }

        get("/airports") {
            val city = call.parameters["city"]
            if (city == null) {
                val airports = dao.getAirports()
                if (airports.isEmpty()) {
                    call.respond(HttpStatusCode.OK, "No cities found")
                }
                val airportRes = ArrayList<Airport>()
                airports.forEach {
                    airportRes.add(Airport(it.code, it.name, it.city, it.coordinates, it.timezone))
                }
                call.respond(HttpStatusCode.OK, airportRes)
            } else {
                val airports = dao.getAirportsWithCity(city)
                if (airports.isEmpty()) {
                    call.respond(HttpStatusCode.OK, "No cities found")
                }
                val airportRes = ArrayList<Airport>()
                airports.forEach {
                    airportRes.add(Airport(it.code, it.name, it.city, it.coordinates, it.timezone))
                }
                call.respond(HttpStatusCode.OK, airportRes)
            }
        }

        get("/airports/inbound") {
            val airportCode = call.parameters["airport"]
            if (airportCode == null) {
                call.respond(HttpStatusCode.BadRequest, "You didn't specified airport")
            }
            val flights = dao.getInboundSchedule(airportCode!!)
            println(flights)
            if (flights.isEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "No inbound flights for airport ${call.request.queryParameters["airport"]}"
                )
            }
            val flightRes = ArrayList<Flight>()
            flights.forEach {
                flightRes.add(
                    Flight(
                        it.flightNo,
                        it.departureAirport,
                        it.departureCity,
                        it.arrivalAirport,
                        it.arrivalCity,
                        it.days
                    )
                )
            }
            call.respond(HttpStatusCode.OK, flightRes)
        }

        get("/airports/outbound") {
            val airportCode = call.parameters["airport"]
            if (airportCode == null) {
                call.respond(HttpStatusCode.BadRequest, "You didn't specified airport")
            }
            val flights = dao.getOutboundSchedule(airportCode!!)
            println(flights)
            if (flights.isEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "No inbound flights for airport ${call.request.queryParameters["airport"]}"
                )
            }
            val flightRes = ArrayList<Flight>()
            flights.forEach {
                flightRes.add(
                    Flight(
                        it.flightNo,
                        it.departureAirport,
                        it.departureCity,
                        it.arrivalAirport,
                        it.arrivalCity,
                        it.days
                    )
                )
            }
            call.respond(HttpStatusCode.OK, flightRes)
        }


    }

}
