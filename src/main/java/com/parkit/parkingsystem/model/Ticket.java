package com.parkit.parkingsystem.model;

import java.math.BigDecimal;
import java.util.Date;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	// ------------
	private BigDecimal price;
	// private double price;

	public static Date inTime;
	public static Date outTime;
	public static boolean discount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public BigDecimal getPrice() {
		return price;
	}

	// public double getPrice() {
	// return price;
	// }

	// public void setPrice(Double price) {
	// this.price = price;
	// }

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public static Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public static Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}

	public void setDiscount(Boolean discount) {
		this.discount = discount;
	}

	public boolean getDiscount() {
		// TODO Auto-generated method stub
		// return true;
		// System.out.println("discount ticket = -------------------------" + discount);
		return discount;
	}

	// public boolean getDiscount() {S
	// TODO Auto-generated method stub
	// return discount;
	// }

}
