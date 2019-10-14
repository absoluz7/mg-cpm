package com.domyjob.cpm;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Component
public class ParkingService {
    private static Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    private long currentTicketNumber = 5000;
    private LinkedList<ParkingSpace> parkingSpaces;

    //Initialises empty car park and file reader
    public void initParkingSystem() {
        parkingSpaces = new LinkedList<>();
        IntStream.range(1, 11).forEach(i -> {
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId("P" + i);
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
            LOG.info("Encountered problems reading {}, check the error log: /n {}", filePath, exception);
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
        ticket.setTicketNumber(currentTicketNumber);
        currentTicketNumber++;

        parkingSpace.setCar(car);
        parkingSpace.setTicket(ticket);
        parkingSpace.setState(true);
        parkingSpaces.set(spaceNumber, parkingSpace);
        LOG.info("PARKED - Car with license plate: {} in space: {} with ticket number: {}",
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

        int spaceNumber = Integer.parseInt(parkingSpace.getId().substring(1))-1;
        LOG.info("UNPARKED - Car with license plate: {} from space: {}", parkingSpace.getCar().getLicensePlate(), parkingSpace.getId());

        parkingSpace.setCar(new Car());
        parkingSpace.setTicket(new Ticket());
        parkingSpace.setState(false);
        parkingSpaces.set(spaceNumber, parkingSpace);
    }

    /**
     * Rearranges the car park by parking space status (taken/free)
     */
    private void compactCarPark() {
        LinkedList<ParkingSpace> newParkingSpaces = new LinkedList<>();
        parkingSpaces.stream().filter(ParkingSpace::getState).forEach(newParkingSpaces::add);
        parkingSpaces.stream().filter(space -> !space.getState()).forEach(newParkingSpaces::add);
        newParkingSpaces.forEach(parkingSpace -> parkingSpace.setId("P" + (newParkingSpaces.indexOf(parkingSpace) + 1)));
        LOG.info("COMPACTED - Car park has been updated.");
        showAvailability(newParkingSpaces);
    }

    /**
     * Prints the car park availability
     * @param spaces
     */
    private void showAvailability(LinkedList<ParkingSpace> spaces) {
        LOG.info("*****CAR PARK AVAILABILITY*****");
        spaces.forEach(space -> LOG.info("Space: {} - Status: {}", space.getId(), space.getState()?"Taken":"Free"));
        spaces.forEach(spa -> LOG.info("Space {} ticket {}",spa.getId(), spa.getTicket().getTicketNumber()));
    }
}