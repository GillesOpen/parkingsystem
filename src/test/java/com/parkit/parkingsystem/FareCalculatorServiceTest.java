package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private BigDecimal fortyFiveMinutesTimes = new BigDecimal("0.75");
	private BigDecimal oneDay = new BigDecimal("24");
	private BigDecimal free = new BigDecimal("0");

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@DisplayName("Calculate Fare Car One Hour / Calculer le prix d’une heure")
	@Test
	// voiture rentrée une heure avant donc -1h
	public void calculateFareCar() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// ce n'est pas un habitué
		Boolean recurrent = false;

		// double y = 1.5;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		// calculer prix pour un non habitué
		fareCalculatorService.calculateFare(ticket);

		// System.out.println(
		// " 1) Fare.CAR_RATE_PER_HOUR) = -------------"
		// + Fare.CAR_RATE_PER_HOUR.setScale(2,
		// RoundingMode.HALF_EVEN));
		// System.out.println("1h = -----------" + ticket.getPrice());

		assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@DisplayName("Calculate Fare Bike One Hour")
	@Test
	public void calculateFareBike() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}

	@DisplayName("Calculate Fare Unknown Type / Calculer le type de tarif inconnu")
	@Test
	public void calculateFareUnknownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		// ce n'est pas ni un velo ni auto donc exception
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@DisplayName("Calculate Fare Bike With Future in Time")
	@Test
	public void calculateFareBikeWithFutureInTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		// date entre superieur a date de sortie donc execption
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@DisplayName("Calculate Fare Bike With Less Than One Hour Parking Time")
	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		Date inTime = new Date();
		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((Fare.BIKE_RATE_PER_HOUR.multiply(fortyFiveMinutesTimes).setScale(2,
				RoundingMode.HALF_EVEN)), ticket.getPrice());
		// ce n'est pas un habitué moins du heure en velo
	}

	@DisplayName("Calculate Fare Car With Less Than One Hour Parking Time "
			+ "/ Calculer le prix d’une voiture avec moins d’une heure de stationnement ")
	// Calculer le prix d’une voiture avec moins d’une heure de stationnement
	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		Date inTime = new Date();
		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Boolean recurrent = false;
		// Boolean recurrent = true;
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		// double X = 1.125;

		// System.out.println(
		// " 2) Fare.CAR_RATE_PER_HOUR.multiply(fortyFiveMinutesTimes) = -------------"
		// + Fare.CAR_RATE_PER_HOUR.multiply(fortyFiveMinutesTimes).setScale(3,
		// RoundingMode.HALF_EVEN));
		// System.out.println("1h pour 0,75 / 45 minutes = -----------" + X);

		// problème d'égalité alors qu'il y a égalité
		// assertEquals
		// (Fare.CAR_RATE_PER_HOUR.multiply(fortyFiveMinutesTimes).setScale(3,
		// RoundingMode.HALF_EVEN), X);

		assertEquals(Fare.CAR_RATE_PER_HOUR.multiply(fortyFiveMinutesTimes).setScale(2,
				RoundingMode.HALF_EVEN), ticket.getPrice());
		// System.out.println("ticket.getPrice() = -----------" + ticket.getPrice());
	}

	@DisplayName("Calculate Fare Car With More Than One Day Parking Time "
			+ "/ Calculer le prix d’une voiture avec plus d’une journée de stationnement")
	@Test
	public void calculateFareCarWithMoreThanOneDayParkingTime() {
		Date inTime = new Date();
		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((Fare.CAR_RATE_PER_HOUR.multiply(oneDay).setScale(2, RoundingMode.HALF_EVEN)),
				ticket.getPrice());
		// reste plus d'un jour
	}

	// free parking less than 30 minutes
	@DisplayName("Calculate Fare Bike With Less Than half An Hour Parking Time")
	@Test
	public void calculateFareBikeWithLessThanhalfAnHourParkingTime() {
		Date inTime = new Date();
		// 29 minutes parking time should give free parking fare
		inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		// System.out.println(
		// "3) free.setScale(2, RoundingMode.HALF_EVEN) = -------------"
		// + free.setScale(2, RoundingMode.HALF_EVEN));
		// System.out.println("ticket.getPrice() = -----------" +
		// ticket.getPrice().setScale(2,
		// RoundingMode.HALF_EVEN));

		assertEquals(free.setScale(2, RoundingMode.HALF_EVEN), ticket.getPrice().setScale(2,
				RoundingMode.HALF_EVEN));
		// velo pas habitué moins de 30 minutes prix à zero
	}

	@DisplayName("Calculate Fare Car With Less Than half An Hour Parking Time "
			+ "/ Calculer le prix d’une voiture avec moins d’une demi-heure de stationnement")
	@Test
	public void calculateFareCarWithLessThanhalfAnHourParkingTime() {
		Date inTime = new Date();
		// 29 minutes parking time should give free parking fare
		inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Boolean recurrent = false;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(free.setScale(2, RoundingMode.HALF_EVEN), ticket.getPrice());
	}

	// 5% recurring user reduction / Réduction de 5 % des utilisateurs récurrents
	@DisplayName("Calculate Fare Bike Recurrent")
	@Test
	public void calculateFareBikeRecurrent() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Boolean recurrent = true;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(recurrent);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((Fare.BIKE_RATE_PER_HOUR.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2,
				RoundingMode.HALF_EVEN)), ticket.getPrice());
		// 5% réduc velo pour 1heure
	}

	@DisplayName("Calculate Fare Car Recurrent "
			+ "/ Calculer les tarifs récurrents des voitures")
	@Test
	public void calculateFareCarRecurrent() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Boolean discount = true;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setDiscount(discount);
		// System.out.println("recurrent 1 du 4 = -----------" + discount);
		fareCalculatorService.calculateFare(ticket);

		// System.out.println("recurrent 2 du 4= -----------" + discount + "----" +
		// ticket.getDiscount());
		// System.out.println(
		// "4) Fare.CAR_RATE_PER_HOUR.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2,
		// RoundingMode.HALF_EVEN) = -------------"
		// + Fare.CAR_RATE_PER_HOUR.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2,
		// RoundingMode.HALF_EVEN));
		// System.out.println("ticket.getPrice() = -----------" + ticket.getPrice());

		assertEquals(Fare.CAR_RATE_PER_HOUR.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2,
				RoundingMode.HALF_EVEN), ticket.getPrice());

	}
}