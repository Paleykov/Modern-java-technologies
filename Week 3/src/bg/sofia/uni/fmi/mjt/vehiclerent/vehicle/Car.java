package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Car extends Vehicle {
    private FuelType fuelType;
    private int numberOfSeats;
    private double pricePerWeek;
    private double pricePerDay;
    private double pricePerHour;

    private static final int PRICE_PER_SEAT = 5;

    public Car(String id, String model, FuelType fuelType, int numberOfSeats, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
        this.pricePerWeek = pricePerWeek;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException, VehicleNotRentedException {
        super.returnBack(rentalEnd);

        this.rentedBy = null;
        this.rentStartTime = null;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        if (startOfRent == null || endOfRent == null) {
            throw new InvalidRentingPeriodException("Rental time cannot be null");
        }

        if (startOfRent.isAfter(endOfRent)) {
            throw new InvalidRentingPeriodException("End time cannot be after start time.");
        }

        Duration rentalDuration = Duration.between(startOfRent, endOfRent);
        long totalMinutes = rentalDuration.toMinutes();
        long totalHours = (long) Math.ceil(totalMinutes / 60.0);
        long weeks = totalHours / (24 * 7);
        long remainingHoursAfterWeeks = totalHours % (24 * 7);

        long days = remainingHoursAfterWeeks / 24;
        long hours = remainingHoursAfterWeeks % 24;

        double basePrice = 0;

        basePrice += weeks * pricePerWeek;
        basePrice += days * pricePerDay;
        basePrice += hours * pricePerHour;

        long totalDays = (long) Math.ceil(totalHours / 24.0);
        double fuelFee = fuelType.getDailyFee() * totalDays;
        double seatFee = numberOfSeats * PRICE_PER_SEAT;

        return basePrice + fuelFee + seatFee;
    }
}
