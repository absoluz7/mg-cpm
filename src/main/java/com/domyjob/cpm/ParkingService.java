package com.domyjob.cpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Component
public class ParkingService {
    private static Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    @Autowired
    private Car car;
    @Autowired
    private ParkingSpace parkingSpace;
    @Autowired
    private Ticket ticket;

    private long currentTicketNumber = 4999;
    private HashMap<String, String> parkingMap;

    //Initialises empty car park and file reader
    public void initParkingSystem() {
        parkingMap = new HashMap<>();
        for (int i=1; i<=10; i++) {
            parkingMap.put("P"+i, "");
        }
        LOG.info("This is the parking map " + parkingMap);

        processCarParkAgenda();
    }

    public void processCarParkAgenda() {

    }

    private Long parkCar(String licensePlate) {
        car.setLicensePlate(licensePlate);
        ticket.setTicketNumber(currentTicketNumber+1);
        return ticket.getTicketNumber();
    }

    private void unparkCar(Long ticketNumber) {

    }

}
