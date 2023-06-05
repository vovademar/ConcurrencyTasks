package ru.nsu.medvedev.database

import ru.nsu.medvedev.entities.Airport
import ru.nsu.medvedev.entities.City
import ru.nsu.medvedev.entities.Flight
import ru.nsu.medvedev.entities.RoutePaths
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

    fun getRoutes(departureAirport:String, arrivalAirport: String, fromDate: String, toDate: String,
                  seatType: String, countOfBounds: String): MutableSet<RoutePaths>{
        val routes: MutableSet<RoutePaths> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("""
            |select distinct arrivalAirport,
       path,
       tf.fare_conditions          as seatType,
       flights.scheduled_departure as departureDate,
       flights.scheduled_arrival   as arrivalDate
from (with recursive branch as (select r.flight_no                       as flightNo,
                                       r.arrival_airport                 as arrivalAirport,
                                       r.departure_airport               as departureAirport,
                                       cast(r.flight_no as varchar(100)) as path,
                                       0                                    cnt
                                from routes r
                                where r.departure_airport = '$departureAirport'
                                union all
                                select t.flight_no,
                                       t.arrival_airport,
                                       t.departure_airport,
                                       cast(b.path || '->' || t.flight_no as varchar(100)),
                                       b.cnt + 1
                                from branch b
                                         join routes t on t.departure_airport = b.arrivalAirport
                                where b.departureAirport <> '$arrivalAirport'
                                  and b.cnt <= $countOfBounds)
      select *
      from branch s
      where s.arrivalAirport = '$arrivalAirport'
        and s.cnt = $countOfBounds) rec
         join flights on flights.flight_no = rec.flightNo
    and flights.scheduled_arrival < '$toDate'
    and flights.scheduled_departure > '$fromDate'
         join ticket_flights tf on flights.flight_id = tf.flight_id and tf.fare_conditions = '$seatType';""".trimMargin())
        while (queryRes.next()){
            routes.add(
                RoutePaths(queryRes.getString("arrivalairport"), queryRes.getString("path"),
                queryRes.getString("seattype"),
                queryRes.getString("departuredate"),
                queryRes.getString("arrivaldate"))
            )
        }
        return routes
    }


//    select *
//    from (with recursive branch as (select r.flight_no,
//    r.arrival_airport,
//    r.departure_airport,
//    cast(r.flight_no as varchar(100)) as path,
//    0                                    cnt
//    from routes r
//
//    where r.departure_airport = 'OVB'
//    union all
//    select t.flight_no,
//    t.arrival_airport,
//    t.departure_airport,
//    cast(b.path || '->' || t.flight_no as varchar(100)),
//    b.cnt + 1
//    from branch b
//    join routes t on t.departure_airport = b.arrival_airport
//    where b.departure_airport <> 'DME'
//    and b.cnt < 3)
//    select *
//    from branch s
//    where s.arrival_airport = 'DME'
//    and s.cnt = 1) rec join flights on flights.flight_no = rec.flight_no
//    and flights.scheduled_arrival < '2017-09-11'
//    and flights.scheduled_departure > '2017-09-09'
//    join ticket_flights tf on flights.flight_id = tf.flight_id and tf.fare_conditions = 'Business'
//

}