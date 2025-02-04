
package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static ParkingService parkingService;
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;

	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService = new DataBasePrepareService();
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	// TODO: check that a ticket is actualy saved in DB and Parking table is updated
	// with availability

	@Test
	public void testParkingACar() {
		parkingService.processIncomingVehicle();
		// check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		assertEquals(ticketDAO.getOldTicket("ABCDEF"), true);
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		assertEquals(parkingSpot.isAvailable(), false);
	}

	// TODO: check that the fare generated and out time are populated correctly in
	// the database
	@Test
	public void testParkingLotExit() {
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// check that the fare generated and out time are populated correctly in
		// the database
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		BigDecimal price = ticket.getPrice();
		Date outTime = ticket.getOutTime();

		assertNotNull(price);
		assertNotNull(outTime);

	}

	// test pour un utilisateur recurrent dans la base
	@Test
	public void testRecurrentUserInDataBase() {
		// ARRANGE
		// first time
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// Second time
		parkingService.processIncomingVehicle();

		// ACT
		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		// ASSERT
		assertEquals(true, ticket.getDiscount());
		// assertEquals(false, ticket.isDiscount());
	}

	@Test
	public void testFreePriceInDataBase() {
		// ARRANGE
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// ACT
		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		// ASSERT
		// free_fare car durée inférieure à 30min
		assertEquals(Fare.FREE_FARE, ticket.getPrice().setScale(2, RoundingMode.HALF_EVEN));
	}
}