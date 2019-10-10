package com.domyjob.cpm;

import org.springframework.stereotype.Component;

@Component
public class ParkingSpace {

    private int id;
    // true for parked and false for un-parked
    private boolean state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
