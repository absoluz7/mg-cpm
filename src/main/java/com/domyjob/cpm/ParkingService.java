package com.domyjob.cpm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ParkingService {

    @Autowired
    private Car car;
    @Autowired
    private ParkingSpace parkingSpace;
    @Autowired
    private Ticket ticket;

    private long currentTicketNumber = 4999;

    public void initParkingSystem() {

    }

    private Long parkCar(String licensePlate) {
        car.setLicensePlate(licensePlate);
        ticket.setTicketNumber(currentTicketNumber+1);
        return ticket.getTicketNumber();
    }

}
