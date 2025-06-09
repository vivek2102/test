package org.example.flightapi.controller;


import org.example.flightapi.model.Flight;
import org.example.flightapi.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService service;

    @GetMapping
    public List<Flight> getBySourceAndDest(@RequestParam String source, @RequestParam String dest) {
        return service.getByRoute(source, dest);
    }

    @GetMapping("/{flightNumber}")
    public Flight getByNumber(@PathVariable String flightNumber) {
        return service.getByNumber(flightNumber);
    }

    @PostMapping
    public String addFlight(@RequestBody Flight f) throws IOException {
        return service.addFlight(f) ? "Flight added" : "Flight already exists";
    }

    @PutMapping("/{flightNumber}")
    public String updateFlight(@PathVariable String flightNumber, @RequestBody Flight updated) throws IOException {
        return service.updateFlight(flightNumber, updated) ? "Flight updated" : "Flight not found";
    }

    @DeleteMapping("/{flightNumber}")
    public String deleteFlight(@PathVariable String flightNumber) throws IOException {
        return service.deleteFlight(flightNumber) ? "Flight deleted" : "Flight not found";
    }
}
