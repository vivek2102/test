package org.example.flightapi.service;

import com.opencsv.*;
import org.example.flightapi.model.Flight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {

    @Value("${flights.csv.path}")
    private String csvPath;

    private List<Flight> flights = new ArrayList<>();

    @PostConstruct
    public void loadFlights() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("flights.csv")) {
            if (inputStream == null) {
                throw new FileNotFoundException("flights.csv not found in resources");
            }
            InputStreamReader reader = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();
            flights.clear();
            for (int i = 1; i < records.size(); i++) {
                String[] data = records.get(i);
                Flight f = new Flight();
                f.setFlightNumber(data[0]);
                f.setSource(data[1]);
                f.setDestination(data[2]);
                f.setDepartureTime(data[3]);
                f.setArrivalTime(data[4]);
                flights.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveFlights() throws IOException {
        try (Writer writer = new FileWriter(csvPath);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = {"flight_number", "source", "destination", "departure_time", "arrival_time"};
            csvWriter.writeNext(header);
            for (Flight f : flights) {
                csvWriter.writeNext(new String[]{
                        f.getFlightNumber(), f.getSource(), f.getDestination(),
                        f.getDepartureTime(), f.getArrivalTime()
                });
            }
        }
    }

    public List<Flight> getAll() {
        return flights;
    }

    public Flight getByNumber(String flightNumber) {
        return flights.stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElse(null);
    }

    public List<Flight> getByRoute(String source, String dest) {
        return flights.stream()
                .filter(f -> f.getSource().equalsIgnoreCase(source) &&
                        f.getDestination().equalsIgnoreCase(dest))
                .collect(Collectors.toList());
    }

    public boolean addFlight(Flight f) throws IOException {
        if (getByNumber(f.getFlightNumber()) != null) return false;
        flights.add(f);
        saveFlights();
        return true;
    }

    public boolean updateFlight(String flightNumber, Flight updated) throws IOException {
        Flight existing = getByNumber(flightNumber);
        if (existing == null) return false;
        existing.setSource(updated.getSource());
        existing.setDestination(updated.getDestination());
        existing.setDepartureTime(updated.getDepartureTime());
        existing.setArrivalTime(updated.getArrivalTime());
        saveFlights();
        return true;
    }

    public boolean deleteFlight(String flightNumber) throws IOException {
        boolean removed = flights.removeIf(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber));
        if (removed) saveFlights();
        return removed;
    }
}
