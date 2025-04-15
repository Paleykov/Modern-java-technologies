package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Bicycle extends Vehicle {
    private Double pricePerHour;
    private Double pricePerDay;

    public Bicycle(String id, String model, double pricePerDay, double pricePerHour) {
        super(id, model);

        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException, VehicleNotRentedException {
        super.returnBack(rentalEnd);

        LocalDateTime latestAllowed = this.getRentedStartTime().plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59);
        if (rentalEnd.isAfter(latestAllowed)) {
            throw new InvalidRentingPeriodException("Bicycles cannot be rented for more than 7 days.");
        }

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

        LocalDateTime maxAllowedEnd = startOfRent.plusDays(7);
        if (endOfRent.isAfter(maxAllowedEnd)) {
            throw new InvalidRentingPeriodException("Cannot rent for more than 7 days.");
        }

        Duration rentalDuration = Duration.between(startOfRent, endOfRent);
        long totalMinutes = rentalDuration.toMinutes();

        if (totalMinutes < 60) {
            return this.pricePerHour;
        }

        if (totalMinutes < 24 * 60) {
            int hours = (int) Math.ceil(totalMinutes / 60.0);
            return hours * pricePerHour;
        } else {

            int days = (int) Math.ceil(totalMinutes / (60.0 * 24));
            return days * pricePerDay;
        }
    }
}
