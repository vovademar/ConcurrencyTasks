# D5

- **Task**

  Design the RESTful web service to handle the following requests:

    - List all the available source and destination cities
    - List all the available source and destination airports
    - List the airports within a city
    - List the inbound schedule for an airport:
        - Days of week
        - Time of arrival
        - Flight no
        - Origin
    - List the outbound schedule for an airport:
        - Days of week
        - Time of departure
        - Flight no
        - Destination
    - List the routes connecting two *points*
        - *Point* might be either an *airport* or a *city*. In the latter case we should search for the flights connecting any airports within the city
        - The mandatory “departure date” parameter limits the flights by the ones departing between 0:00:00 of the specified date and 0:00:00 of the next date
        - The “booking class” parameter should be one of the 'Economy', 'Comfort', 'Business'
        - Additional parameter limits the number of connections: 0 (direct), 1, 2, 3, unbound
    - Create a booking for a selected route for a single passenger
    - Online check-in for a flight

- List all the available source and destination cities

  `GET /cities`

    - *`return`* a list of cities

  Status code

    - Status code 200 OK

        ```json
        [
          {
            "name": "New York"
          },
          {
            "name": "London"
          },
          ...
        ]
        ```

    - Status code 400 bad request
        - `"error" = "Cities are empty"`

- List all the available source and destination airports

  `GET /airports`

    - *return* list of airports

  Status code

    - Status code 200 OK

        ```json
        [
          {
            "code": "YKS",
            "name": "Якутск",
        		"city": "Якутск",
        		"coordinates": "(129.77099609375,62.093299865722656)",
        		"timezone": "Asia/Yakutsk"
          },
          {
            "code": "KHV",
            "name": "Хабаровск-Новый",
        		"city": "Хабаровск",
        		"coordinates": "(135.18800354004,48.52799987793)",
        		"timezone": "Asia/Vladivostok"
          },
          ...
        ]
        ```

    - Status code 400 bad request
        - `"error" = "Airports are empty"`

- List the airports within a city

  `GET /airports?city=<Москва>`

    - **city** - parameter that represents the city name.
    - *return* list of airports

  Status code:

    - Status code 200 OK

        ```json
        [
          {
                "code": "SVO",
                "name": "Шереметьево",
                "city": "Москва",
                "coordinates": "(37.4146,55.972599)",
                "timezone": "Europe/Moscow"
            },
            {
                "code": "VKO",
                "name": "Внуково",
                "city": "Москва",
                "coordinates": "(37.2615013123,55.5914993286)",
                "timezone": "Europe/Moscow"
            },
          ...
        ]
        ```

    - Status code 400 bad request
        - `"error" = "City not found"`

- List the inbound schedule for an airport

  `GET /airports/inbound?airport=<OVB>`

    - code - parameter that represents the airport code.

  *Return*:

    - A list of flights arriving at the airport on the specified date, with the following information:
        - Flight no
        - departureAirport
        - departureCity
        - arrivalAirport
        - arrivalCity
        - Days of week

  Status code:

    - Status code 200 OK

        ```json
        [
          {
                "flightNo": "PG0006",
                "departureAirport": "PKV",
                "departureCity": "Псков",
                "arrivalAirport": "DME",
                "arrivalCity": "Москва",
                "days": "{1,4,6}"
            },
            {
                "flightNo": "PG0020",
                "departureAirport": "UUA",
                "departureCity": "Бугульма",
                "arrivalAirport": "DME",
                "arrivalCity": "Москва",
                "days": "{1,2,3,4,5,6,7}"
            },
        ]
        ```

    - Status code 400 bad request
        - `"error" = "Airport not found"`

- List the outbound schedule for an airport

  `GET /airport/outbound?airport=<OVB>`

    - code - parameter that represents the airport code.

  *Return*:

    - A list of flights arriving at the airport on the specified date, with the following information:
        - Flight no
        - departureAirport
        - departureCity
        - arrivalAirport
        - arrivalCity
        - Days of week

  Status code:

    - Status code 200 OK

        ```json
        [
          {
                "flightNo": "PG0083",
                "departureAirport": "OVB",
                "departureCity": "Новосибирск",
                "arrivalAirport": "PYJ",
                "arrivalCity": "Удачный",
                "days": "{1}"
            },
            {
                "flightNo": "PG0186",
                "departureAirport": "OVB",
                "departureCity": "Новосибирск",
                "arrivalAirport": "PEE",
                "arrivalCity": "Пермь",
                "days": "{1,2,3,4,5,6,7}"
            },
        ]
        ```

    - Status code 400 bad request
        - `"error" = "Airport not found"`

- List the routes connecting two *points*

  `GET /routes?fromAirport=OVB&toAirport=DME&fromDate=2017-09-09&toDate=2017-09-11&seatType=Economy&bounds=<0..>`

    - fromAirport - parameter that represents the departure point
    - toAirport - parameter that represents the arrival point
    - fromDate - parameter that represents the flight day
    - toDate - max arrival date
    - seatType - parameter that represents the seat class
    - bound - parameter that represents the count of bounds

  *Return* list of paths

  Status code:

    - Status code 200 OK

        ```json
        [
        {
                "arrivalAirport": "DME",
                "path": "PG0047->PG0048->PG0223",
                "seatType": "Economy",
                "departureDate": "2017-09-09 15:50:00+07",
                "arrivalDate": "2017-09-09 19:15:00+07"
            },
            {
                "arrivalAirport": "DME",
                "path": "PG0047->PG0048->PG0223",
                "seatType": "Economy",
                "departureDate": "2017-09-10 15:50:00+07",
                "arrivalDate": "2017-09-10 19:15:00+07"
            },
        ]
        ```

    - Status code 400 bad request
        - `"error" = "route not found"`

- Create a booking for a selected route for a single passenger

  `PUT /booking`

    - body:

        ```json
        {
          "flightNo": "PG0405",
          "seatType": "Economy",
          "date": "2017-08-25",
          "name": "Valdemar Medvedev",
          "passengerID": "123456789",
          "phone": "+79137777777"
        }
        ```


*Return* number of ticket

Status code:

- Status code 200 OK

    ```json
        {
            "ticketNo": "9150006156494",
            "bookingCode": "BECC2B",
            "flightNo": "PG0405",
            "setType": "Economy",
            "price": "6700.00"
        }
    ```

    - Status code 400 bad request
        - `"error" = "Invalid input"`

- Online check-in for a flight

  `PUT /checkin`

    - Body:

    ```json
        {
          "ticketNo": "9150006156494"
        }
    ```


Return confirm of checkin

Status code:

- Status code 200 OK

    ```json
        {
            "seat": "8F",
            "boardingNo": "6",
            "flightNo": "PG0405"
        }
    ```

- Status code 400 bad request
    - `"error" = "Invalid input"`