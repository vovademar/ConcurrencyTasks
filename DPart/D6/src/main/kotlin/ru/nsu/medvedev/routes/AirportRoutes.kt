//package ru.nsu.medvedev.routes
//
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import ru.nsu.medvedev.entities.Airport
//import ru.nsu.medvedev.entities.airportStorage
//
//fun Route.airportRouting() {
//    route("/airport") {
//        get {
//            if (airportStorage.isNotEmpty()) {
//                call.respond(airportStorage)
//            } else {
//                call.respondText("No customers found", status = HttpStatusCode.OK)
//            }
//        }
//        get("{code?}") {
//            val code = call.parameters["code"] ?: return@get call.respondText(
//                "Missing id",
//                status = HttpStatusCode.BadRequest
//            )
//            val airport =
//                airportStorage.find { it.code == code } ?: return@get call.respondText(
//                    "No customer with id $code",
//                    status = HttpStatusCode.NotFound
//                )
//            call.respond(airport)
//        }
//        post {
//            val airport = call.receive<Airport>()
//            airportStorage.add(airport)
//            call.respondText("Airport stored correctly", status = HttpStatusCode.Created)
//        }
//        delete("{id?}") {
//
//        }
//    }
//}