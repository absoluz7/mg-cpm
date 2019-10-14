package com.domyjob.cpm;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Component
public class ParkingService {
    private static Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    private long currentTicketNumber = 5000;
    private List<ParkingSpace> parkingSpaces;
    private Car car;
    private Ticket ticket;
    private ParkingSpace parkingSpace;

    //Initialises empty car park and file reader
    public void initParkingSystem() {
        parkingSpaces = new LinkedList<>();
      //TODO intStream
        for (int i=1; i<=10; i++) {
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId("P" + i);
            parkingSpace.setState(false);
            parkingSpace.setCar(new Car());
            parkingSpace.setTicket(new Ticket());
            parkingSpaces.add(parkingSpace);
        }
        processParkCommands();
    }

    private void processParkCommands() {
        List<String> commands = getParkCommands();
        commands.forEach(command -> {
//            LOG.info("Command is " + command);
            switch (command.substring(0,1)) {
                case "p":
                    parkCar(command);
                    break;
                case "u":
                    unparkCar(command);
                    break;
                case "c":
                    compactCarPark(command);
                    break;
            }
        });
    }

    //Reads file with parking commands
    private List<String> getParkCommands() {
        String filePath = "parking/park1.txt";

        List<String> parkCommands = new LinkedList<>();
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
     * Parks a car based on license plate and returns a ticket number
     * @param command
     * @return
     */
    private void parkCar(String command) {
        for (ParkingSpace space : parkingSpaces) {
            if (!space.getState()) {
                int spaceNumber = Integer.parseInt(space.getId().substring(1))-1;
                Car car = new Car();
                car.setLicensePlate(command.substring(1));

                Ticket ticket = new Ticket();
                ticket.setTicketNumber(currentTicketNumber);
                currentTicketNumber++;

                space.setCar(car);
                space.setTicket(ticket);
                space.setState(true);
                parkingSpaces.set(spaceNumber, space);
                LOG.info("PARKED - Car with license plate: {} in space: {}", space.getCar().getLicensePlate(), space.getId());
                break;
            }
        }
    }

    private void unparkCar(String command) {
        for (ParkingSpace space : parkingSpaces) {
            if (Long.valueOf(command.substring(1)).equals(space.getTicket().getTicketNumber())) {
                int spaceNumber = Integer.parseInt(space.getId().substring(1))-1;
                LOG.info("UNPARKED - Car with license plate: {} from space: {}", space.getCar().getLicensePlate(), space.getId());
                space.setCar(new Car());
                space.setTicket(new Ticket());
                space.setState(false);
                parkingSpaces.set(spaceNumber, space);
                break;
            }
        }
    }

    private void compactCarPark(String command) {
        for (ParkingSpace space : parkingSpaces) {
            if (!space.getState()) {
                int spaceNumber = Integer.parseInt(space.getId().substring(1))-1;

            }
        }
    }

}
