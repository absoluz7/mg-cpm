package com.domyjob.cpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@Component
public class ParkingService {
    private static Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    private long currentTicketNumber = 5000;
    private final LinkedList<String> parkingIds = new LinkedList<>(Arrays.asList("P1","P2","P3","P4","P5","P6","P7","P8","P9","P10"));
    private LinkedList<ParkingSpace> parkingSpaces;
    private LinkedHashSet<Long> usedTicketNumbers;

    //Initialises empty car park and file reader
    public void initParkingSystem() {
        parkingSpaces = new LinkedList<>();
        usedTicketNumbers = new LinkedHashSet<>();
        parkingIds.forEach(id -> {
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId(id);
            parkingSpace.setState(false);
            parkingSpace.setCar(new Car());
            parkingSpace.setTicket(new Ticket());
            parkingSpaces.add(parkingSpace);
        });
        processParkCommands();
    }

    private void processParkCommands() {
        LinkedList<String> commands = getParkCommands();
        commands.forEach(command -> {
            switch (command.substring(0,1)) {
                case "p":
                    parkCar(command);
                    break;
                case "u":
                    unparkCar(command);
                    break;
                case "c":
                    compactCarPark();
                    break;
            }
        });
    }

    /**
     * Reads file with parking commands
     */
    private LinkedList<String> getParkCommands() {
        String filePath = "parking/park1.txt";

        LinkedList<String> parkCommands = new LinkedList<>();
        Stream<String> stream = Stream.of("");
        try {
            LOG.info("Reading Park File from {}", filePath);
            Path path = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
            stream = Files.lines(path);
            stream.flatMap(string -> Stream.of(string.split(","))).forEach(parkCommands::add);
        } catch (IOException | URISyntaxException exception) {
            LOG.info("Encountered problems reading {}, check the error log: {}", filePath, exception);
        } finally {
            stream.close();
        }
        return parkCommands;
    }

    /**
     * Parks a car based on license plate
     * @param command
     */
    private void parkCar(String command) {
        ParkingSpace parkingSpace = parkingSpaces.stream()
            .filter(space -> !space.getState())
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Car park full, wait for space to become available."));

        int spaceNumber = Integer.parseInt(parkingSpace.getId().substring(1))-1;

        Car car = new Car();
        car.setLicensePlate(command.substring(1));

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(getUniqueTicket());

        parkingSpace.setCar(car);
        parkingSpace.setTicket(ticket);
        parkingSpace.setState(true);
        parkingSpaces.set(spaceNumber, parkingSpace);
        LOG.info("PARKED - Car plate: {} in space: {} with ticket number: {}",
            parkingSpace.getCar().getLicensePlate(), parkingSpace.getId(), parkingSpace.getTicket().getTicketNumber());
    }

    /**
     * Unparks a car based on the ticket number
     * @param command
     */
    private void unparkCar(String command) {
        ParkingSpace parkingSpace = parkingSpaces.stream()
            .filter(space -> Long.valueOf(command.substring(1)).equals(space.getTicket().getTicketNumber()))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Car could not be unparked, please check your ticket."));

        LOG.info("UNPARKED - Car plate: {} from space: {}", parkingSpace.getCar().getLicensePlate(), parkingSpace.getId());

        parkingSpace.setCar(new Car());
        parkingSpace.setTicket(new Ticket());
        parkingSpace.setState(false);
    }

    /**
     * Rearranges the car park by parking space status (taken/free)
     */
    private void compactCarPark() {
        LinkedList<ParkingSpace> newParkingSpaces = new LinkedList<>();
        parkingSpaces.stream().filter(ParkingSpace::getState).forEach(newParkingSpaces::add);
        parkingSpaces.stream().filter(space -> !space.getState()).forEach(newParkingSpaces::add);
        parkingSpaces = newParkingSpaces;

        parkingSpaces.forEach(parkingSpace -> parkingSpace.setId(parkingIds.get(parkingSpaces.indexOf(parkingSpace))));
        LOG.info("COMPACTED - Car Park Update");
        showAvailability(parkingSpaces);
    }

    /**
     * Prints the car park availability
     * @param spaces
     */
    private void showAvailability(List<ParkingSpace> spaces) {
        LOG.info("======CAR PARK AVAILABILITY======");
        spaces.forEach(space -> LOG.info("Space: {} - Status: {}", space.getId(), space.getState()?"Taken - Ticket: "
                + space.getTicket().getTicketNumber():"Free"));
        LOG.info("=================================");
    }

    /**
     * @return unique ticket number
     */
    private long getUniqueTicket() {
        while (usedTicketNumbers.contains(currentTicketNumber)) {
            currentTicketNumber++;
        }
        usedTicketNumbers.add(currentTicketNumber);
        return currentTicketNumber;
    }
}