package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	/**
	 * Used to calculate the fare when the user exiting the parking. If the user
	 * exiting before 30 minutes, it's free. If the user isRecurrentUser, 5% of
	 * reduction.
	 *
	 * Sert à calculer le tarif lorsque l’utilisateur quitte le stationnement. Si
	 * l’utilisateur quitte avant 30 minutes, c’est gratuit. Si l’utilisateur est en
	 * mode d’actualisation, 5 % de réduction.
	 *
	 * @param ticket
	 * @param isRecurrentUser if true, 5% reduction
	 */

	// public void calculateFare(Ticket ticket) {
	// if ((ticket.getOutTime() == null) ||
	// (ticket.getOutTime().before(ticket.getInTime()))) {
	// throw new IllegalArgumentException("Out time provided is incorrect:" +
	// ticket.getOutTime().toString());
	// }

	// int inHour = ticket.getInTime().getHours();
	// int outHour = ticket.getOutTime().getHours();

	// TODO: Some tests are failing here. Need to check if this logic is correct
	// Certains tests échouent ici. Il faut vérifier si cette logique est correcte
	// int duration = outHour - inHour;

	// switch (ticket.getParkingSpot().getParkingType()){
	// case CAR: {
	// ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
	// break;
	// }
	// case BIKE: {
	// ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
	// break;
	// }
	// default: throw new IllegalArgumentException("Unkown Parking Type"

	public void calculateFare(Ticket ticket) {

		// System.out.println("calculateFare = -------------------------" +
		// ticket.getDiscount());

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException(
					"Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// Convert into hours / Convertir en heures
		double inHour = (ticket.getInTime().getTime()) / 3600000.0;
		double outHour = (ticket.getOutTime().getTime()) / 3600000.0;

		// Calculate the difference in order to obtain the duration
		// Calculer la différence pour obtenir la durée
		double duree = (outHour - inHour);

		// Convert double into bigDecimal for the rest of the monetary calculations
		// Convertir le double en bigDecimal pour le reste des calculs monétaires
		BigDecimal duration = new BigDecimal(duree);

		// Create a variable representing the limit of a free half hour
		// Créer une variable représentant la limite d’une demi-heure gratuite
		BigDecimal demiHeure = new BigDecimal("0.5");

		// Test if the user stayed less than 30 minutes
		// Tester si l’utilisateur est resté moins de 30 minutes
		int limiteDemiHeure = duration.compareTo(demiHeure);

		// If test == -1 it means that the duration was much less than 30 minutes
		// Si test == -1 cela signifie que la durée était bien inférieure à 30 minutes
		if (limiteDemiHeure == -1) {
			duration = Fare.FREE_FARE;
		}

		// Test if the getRecurrent method returns to true and if so the user
		// benefits from the reduction otherwise the calculation of the normal price is
		// performed

		// Tester si la méthode getRecurrent revient à true et si oui l’utilisateur
		// bénéficie de la réduction sinon le calcul du prix normal est exécuté
		// System.out.println("recurrent case car if 1 = -------------------------" +
		// ticket.getDiscount());
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			if (ticket.getDiscount()) {
				ticket.setPrice((duration.multiply(Fare.CAR_RATE_PER_HOUR
						.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2, RoundingMode.HALF_EVEN))));
				// System.out.println("car getDiscount = -----------" + ticket.getDiscount());
			} else {
				ticket.setPrice(
						duration.multiply(Fare.CAR_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_EVEN));
				// System.out.println("car not getdiscount= -----------" +
				// ticket.getDiscount());
			}
			break;
		}

		case BIKE: {
			if (ticket.getDiscount()) {
				ticket.setPrice((duration.multiply(Fare.BIKE_RATE_PER_HOUR
						.multiply(Fare.REDUCTION_FIVE_POURCENT).setScale(2, RoundingMode.HALF_EVEN))));
			} else {
				ticket.setPrice(
						duration.multiply(Fare.BIKE_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_EVEN));
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
		// System.out.println("recurrent case car if = -------------------------" +
		// ticket.getDiscount());
	}
}
