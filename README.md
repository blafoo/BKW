# BKW

App to visualize the data from server.growatt.com for Growatt inverters

## Usage

1. Apply your login data for server.growatt.com to `src/main/resources/application.properties`
1. Run the Spring Boot app `de.blafoo.bkw.Application`

## Dependencies

1. Spring Boot 3+
1. Vaadin 24+ for the UI
1. (`Growatt API`](https://github.com/blafoo/growatt) to access server.growatt.com

## Remarks

The app accesses server.growatt.com either using Feign or a Spring WebClient