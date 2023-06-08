package ru.nsu.medvedev.database

import ru.nsu.medvedev.entities.*
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class DAO {
    private lateinit var connection: Connection
    fun connectToDB() {
        Class.forName("org.postgresql.Driver")
        val url = "jdbc:postgresql://localhost:5432/demo"
        val user = "postgres"
        val password = "123"
        this.connection = DriverManager.getConnection(url, user, password)
    }

    fun getCities(): MutableSet<City> {
        val cities: MutableSet<City> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT city FROM airports;")
        while (queryRes.next()) {
            cities.add(City(city = queryRes.getString("city")))
        }
        return cities
    }

    fun getAirports(): MutableSet<Airport> {
        val airports: MutableSet<Airport> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM airports;")
        while (queryRes.next()) {
            airports.add(
                Airport(
                    code = queryRes.getString("airport_code"),
                    city = queryRes.getString("city"),
                    name = queryRes.getString("airport_name"),
                    timezone = queryRes.getString("timezone"),
                    coordinates = queryRes.getString("coordinates")
                )
            )
        }
        return airports
    }

    fun getAirportsWithCity(city: String): MutableSet<Airport> {
        val airports: MutableSet<Airport> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM airports WHERE city = '$city';")
        while (queryRes.next()) {
            airports.add(
                Airport(
                    code = queryRes.getString("airport_code"),
                    city = queryRes.getString("city"),
                    name = queryRes.getString("airport_name"),
                    timezone = queryRes.getString("timezone"),
                    coordinates = queryRes.getString("coordinates")
                )
            )
        }
        return airports
    }

    fun getInboundSchedule(code: String): MutableSet<Flight> {
        val flights: MutableSet<Flight> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM routes WHERE arrival_airport = '$code';")
        while (queryRes.next()) {
            flights.add(
                Flight(
                    flightNo = queryRes.getString("flight_no"),
                    departureAirport = queryRes.getString("departure_airport"),
                    departureCity = queryRes.getString("departure_city"),
                    arrivalAirport = queryRes.getString("arrival_airport"),
                    arrivalCity = queryRes.getString("arrival_city"),
                    days = queryRes.getString("days_of_week")
                )
            )
        }
        return flights
    }

    fun getOutboundSchedule(code: String): MutableSet<Flight> {
        val flights: MutableSet<Flight> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery("SELECT * FROM routes WHERE departure_airport = '$code';")
        while (queryRes.next()) {
            flights.add(
                Flight(
                    flightNo = queryRes.getString("flight_no"),
                    departureAirport = queryRes.getString("departure_airport"),
                    departureCity = queryRes.getString("departure_city"),
                    arrivalAirport = queryRes.getString("arrival_airport"),
                    arrivalCity = queryRes.getString("arrival_city"),
                    days = queryRes.getString("days_of_week")
                )
            )
        }
        return flights
    }

    fun getRoutes(
        departureAirport: String, arrivalAirport: String, fromDate: String, toDate: String,
        seatType: String, countOfBounds: String
    ): MutableSet<RoutePaths> {
        val routes: MutableSet<RoutePaths> = mutableSetOf()
        val statement = connection.createStatement()
        val queryRes = statement.executeQuery(
            """
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
         join ticket_flights tf on flights.flight_id = tf.flight_id and tf.fare_conditions = '$seatType';""".trimMargin()
        )
        while (queryRes.next()) {
            routes.add(
                RoutePaths(
                    queryRes.getString("arrivalairport"), queryRes.getString("path"),
                    queryRes.getString("seattype"),
                    queryRes.getString("departuredate"),
                    queryRes.getString("arrivaldate")
                )
            )
        }
        return routes
    }

    private fun checkTicketNo(ticketNo: String, connection: Connection): Boolean {
        val sqlQuery = "select ticket_no from bookings.tickets where ticket_no='$ticketNo';"
        val statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery(sqlQuery)
            if (resSet.next()) {
                connection.rollback()
                connection.autoCommit = true
                return false
            }
            return true
        }
    }

    private fun checkBookRef(bookRef: String, connection: Connection): Boolean {
        val sqlQuery = "select book_ref from bookings.bookings where book_ref='$bookRef';"
        val statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery(sqlQuery)
            if (resSet.next()) {
                connection.rollback()
                connection.autoCommit = true
                return false
            }
            return true
        }
    }

    fun booking(
        flightNo: String, seatType: String, date: String,
        name: String, passengerID: String, phone: String
    ): Ticket {

        /* define flight id related on flightNo & date & flight status */
        var statement = connection.createStatement()
        var flightId = "0"
        val flightIdres = statement.executeQuery(
            "select fl.flight_id flid from flights fl " +
                    "where fl.flight_no = '$flightNo' " +
                    "and fl.status = 'Scheduled' " +
                    "and fl.scheduled_departure >= '$date' limit 1;\n"
        )
        while (flightIdres.next()) {
            flightId = flightIdres.getString("flid")
        }

        /* find out how many seats left */
        var seatsLeft = 0
        val seatsRes = statement.executeQuery(
            "select count(*) - \n" +
                    "(select count(*) from bookings.ticket_flights where fare_conditions = '$seatType' and flight_id = '$flightId') seatsLeft \n" +
                    "from bookings.seats\n" +
                    "where aircraft_code = (select aircraft_code from bookings.flights where flight_id = '$flightId' and fare_conditions = '$seatType');\n"
        )
        while (seatsRes.next()) {
            seatsLeft = seatsRes.getInt("seatsLeft")
        }
        if (seatsLeft == 0) {
            throw RuntimeException("not enough seats")
        }

        /* define price of the ticket */
        var price = "0"
        val priceRes = statement.executeQuery(
            "select amount price from bookings.ticket_flights " +
                    "where flight_id = '$flightId' and fare_conditions = '$seatType' " +
                    "order by amount limit 1;\n"
        )
        while (priceRes.next()) {
            price = priceRes.getString("price")
        }

        /* create and insert bookingUid */
        var bookingUid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase(Locale.getDefault())
        while (!checkBookRef(bookingUid, connection)) {
            bookingUid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase(Locale.getDefault())
        }
        statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery("select count(*) from bookings.bookings where book_ref = '$bookingUid';")
            while (resSet.next()) {
                if (resSet.getInt("count") != 0) {
                    connection.rollback()
                    connection.autoCommit = true
                    throw RuntimeException("we have the same booking_ref")
                }
            }
        }

        /* create and insert ticketNo */
        var ticketNo = (0..9999999999999L).random().toString()
        while (!checkTicketNo(ticketNo, connection)) {
            ticketNo = (0..9999999999999L).random().toString()
        }
        statement = connection.createStatement()
        statement.use {
            val resSet =
                it.executeQuery(
                    "insert into bookings.bookings (book_ref, book_date, total_amount) " +
                            "values ('$bookingUid', now(), $price) returning book_ref;"
                )
            while (resSet.next()) {
                println(resSet.getString("book_ref"))
            }
        }

        /* insert new data in tickets table */
        statement = connection.createStatement()
        statement.use {
            it.executeQuery(
                "insert into bookings.tickets (ticket_no, book_ref, passenger_id, passenger_name, contact_data) " +
                        "values ('$ticketNo', '$bookingUid', '$passengerID', '$name', '{\"phone\": \"$phone\"}') returning *;"
            )
        }

        /* insert new data in ticket_flights table */
        statement = connection.createStatement()
        statement.use {
            it.executeQuery(
                "insert into bookings.ticket_flights (ticket_no, flight_id, fare_conditions, amount) " +
                        "values ('$ticketNo', $flightId, '$seatType', $price) returning *;"
            )
        }

        return Ticket(ticketNo, bookingUid, flightNo, seatType, price)
    }

    fun checkin(ticketNo: String): CheckIn {
        /* check if ticked is already checked */
        var sqlQuery = "select count(*) from bookings.boarding_passes where ticket_no = '$ticketNo';"
        var statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery(sqlQuery)
            while (resSet.next()) {
                if (resSet.getInt("count") != 0) {
                    throw RuntimeException("the ticket is already checked")
                }
            }
        }

        /* define fare condition */
        sqlQuery = "select fare_conditions from ticket_flights where ticket_no='$ticketNo';"
        statement = connection.createStatement()
        var queryRes = statement.executeQuery(sqlQuery)
        var fareCondition = ""
        while (queryRes.next()) {
            fareCondition = queryRes.getString("fare_conditions")
        }

        /* define flightId */
        var flightId = ""
        sqlQuery = "select flight_id from ticket_flights where ticket_no='$ticketNo';"
        statement = connection.createStatement()
        queryRes = statement.executeQuery(sqlQuery)
        if (!queryRes.next()) {
            throw Exception("Sorry, not found ticket for $ticketNo")
        } else {
            flightId = queryRes.getString("flight_id")
        }

        /* define aircraft code */
        sqlQuery = "select aircraft_code from bookings.flights where flight_id = $flightId;"
        var aircraftCode = ""
        statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery(sqlQuery)
            while (resSet.next()) {
                aircraftCode = resSet.getString("aircraft_code")
            }
        }

        /* find free seat */
        sqlQuery = "select seat_no \n" +
                "from bookings.seats \n" +
                "where aircraft_code = '$aircraftCode' and fare_conditions = '$fareCondition' and seat_no not in \n" +
                "(select seat_no from bookings.boarding_passes where flight_id = '$flightId') limit 1;"
        var seat = ""
        statement = connection.createStatement()
        statement.use {
            val resSet = it.executeQuery(sqlQuery)
            while (resSet.next()) {
                seat = resSet.getString("seat_no")
            }
        }

        /* define boardingNo */
        sqlQuery = "select count(*) from boarding_passes where flight_id='$flightId';"
        statement = connection.createStatement()
        queryRes = statement.executeQuery(sqlQuery)
        var boardingNo = 0
        while (queryRes.next()) {
            boardingNo = queryRes.getInt("count")
        }
        boardingNo += 1

        /* insert new info in boarding_passes */
        sqlQuery = "insert into boarding_passes (ticket_no, flight_id, boarding_no, seat_no) " +
                "values ('$ticketNo', '$flightId', '$boardingNo', '$seat') returning *;"
        statement = connection.createStatement()
        statement.use {
            it.executeQuery(sqlQuery)
        }

        /* define flightNo */
        sqlQuery = "select flight_no from flights where flight_id = '$flightId'\n"
        statement = connection.createStatement()
        queryRes = statement.executeQuery(sqlQuery)
        var flightNo = ""
        while (queryRes.next()) {
            flightNo = queryRes.getString("flight_no")
        }

        return CheckIn(seat, boardingNo.toString(), flightNo)
    }

}