package ru.nsu.medvedev.database

import ru.nsu.medvedev.entities.Airport
import ru.nsu.medvedev.entities.City
import ru.nsu.medvedev.entities.Flight
import java.sql.Connection
import java.sql.DriverManager

class DAO {
    private lateinit var connection: Connection
    fun connectToDB() {
        Class.forName("org.postgresql.Driver")
        val url = "jdbc:postgresql://localhost:5432/demo"
        val user = "postgres"
        val password = "123"
        this.connection = DriverManager.getConnection(url, user, password)
    }

     fun getCities(): MutableSet<City>{
        val cities: MutableSet<City> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT city FROM airports;")
        while (queryRes.next()) {
            cities.add(City(city = queryRes.getString("city")))
        }
        return cities
    }

    fun getAirports(): MutableSet<Airport>{
        val airports: MutableSet<Airport> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM airports;")
        while (queryRes.next()){
            airports.add(Airport(code = queryRes.getString("airport_code"),
                city = queryRes.getString("city"),
                name = queryRes.getString("airport_name"),
                timezone = queryRes.getString("timezone"),
                coordinates = queryRes.getString("coordinates")))
        }
        return airports
    }

    fun getAirportsWithCity(city: String): MutableSet<Airport>{
        val airports: MutableSet<Airport> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM airports WHERE city = '$city';")
        while (queryRes.next()){
            airports.add(Airport(code = queryRes.getString("airport_code"),
                city = queryRes.getString("city"),
                name = queryRes.getString("airport_name"),
                timezone = queryRes.getString("timezone"),
                coordinates = queryRes.getString("coordinates")))
        }
        return airports
    }

    fun getInboundSchedule(code: String): MutableSet<Flight>{
        val flights: MutableSet<Flight> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM routes WHERE arrival_airport = '$code';")
        while (queryRes.next()){
            flights.add(Flight(flightNo = queryRes.getString("flight_no"),
                departureAirport = queryRes.getString("departure_airport"),
                departureCity = queryRes.getString("departure_city"),
                arrivalAirport = queryRes.getString("arrival_airport"),
                arrivalCity = queryRes.getString("arrival_city"),
                days = queryRes.getString("days_of_week")))
        }
        return flights
    }

    fun getOutboundSchedule(code: String): MutableSet<Flight>{
        val flights: MutableSet<Flight> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM routes WHERE departure_airport = '$code';")
        while (queryRes.next()){
            flights.add(Flight(flightNo = queryRes.getString("flight_no"),
                departureAirport = queryRes.getString("departure_airport"),
                departureCity = queryRes.getString("departure_city"),
                arrivalAirport = queryRes.getString("arrival_airport"),
                arrivalCity = queryRes.getString("arrival_city"),
                days = queryRes.getString("days_of_week")))
        }
        return flights
    }



}