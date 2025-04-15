package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Caravan extends Car {
    private int numberOfBeds;
    private static final int BED_FEE = 10;

    public Caravan(String id, String model, FuelType fuelType, int numberOfSeats, int numberOfBeds, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats, pricePerWeek, pricePerDay, pricePerHour);
        this.numberOfBeds = numberOfBeds;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {

        long durationInHours = Duration.between(startOfRent, endOfRent).toHours();

        if (durationInHours < 24) {
            throw new InvalidRentingPeriodException("Caravans must be rented for at least 24 hours.");
        }

        double basePrice = super.calculateRentalPrice(startOfRent, endOfRent);
        return basePrice + numberOfBeds * BED_FEE;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException, VehicleNotRentedException {
        super.returnBack(rentalEnd);

        long hours = Duration.between(getRentedStartTime(), rentalEnd).toHours();
        if (hours < 24) {
            throw new InvalidRentingPeriodException("Caravans must be rented for at least 24 hours.");
        }

        this.rentedBy = null;
        this.rentStartTime = null;
    }
}
