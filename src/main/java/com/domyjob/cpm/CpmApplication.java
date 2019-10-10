package com.domyjob.cpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CpmApplication implements CommandLineRunner {
	private static Logger LOG = LoggerFactory.getLogger(CpmApplication.class);

	@Autowired
	private ParkingService parkingService;

	public static void main(String[] args) {
		LOG.info("STARTING APPLICATION...");
		SpringApplication app = new SpringApplication(CpmApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
		LOG.info("APPLICATION SHUTTING DOWN...");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("MANAGING CAR PARK, PLEASE WAIT...");
		parkingService.initParkingSystem();
	}

}
