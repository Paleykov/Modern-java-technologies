package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Vehicle {
    private String id;
    private String model;

    protected Driver rentedBy = null;
    protected LocalDateTime rentStartTime = null;
    // private LocalDateTime rentEndTime = null;

    public Vehicle(String id, String model) {
        this.id = id;
        this.model = model;
    }

    public void setRentedBy(Driver driver) {
        this.rentedBy = driver;
    }

    public Driver getRentedBy() {
        return this.rentedBy;
    }

    public void setRentStartTime(LocalDateTime rentStartTime) {
        this.rentStartTime = rentStartTime;
    }

    public String getId() {
        return this.id;
    }

    public String getModel() {
        return this.model;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRentedStartTime(LocalDateTime rentStartTime) {
        this.rentStartTime = rentStartTime;
    }

    public LocalDateTime getRentedStartTime() {
        return this.rentStartTime;
    }

    /**
     * Simulates rental of the vehicle. The vehicle now is considered rented by the provided driver and the start of the rental is the provided date.
     *
     * @param driver        the driver that wants to rent the vehicle.
     * @param startRentTime the start time of the rent
     * @throws VehicleAlreadyRentedException in case the vehicle is already rented by someone else or by the same driver.
     */
    public void rent(Driver driver, LocalDateTime startRentTime) throws VehicleAlreadyRentedException {
        if (driver == null || startRentTime == null) {
            throw new IllegalArgumentException("Driver or startRentTime cannot be null");
        }

        if (this.rentedBy != null) {
            throw new VehicleAlreadyRentedException("Vehicle is already rentedBy" + this.rentedBy);
        }

        this.rentStartTime = startRentTime;
        this.rentedBy = driver;
    }

    /**
     * Simulates end of rental for the vehicle - it is no longer rented by a driver.
     *
     * @param rentalEnd time of end of rental
     * @throws IllegalArgumentException      in case @rentalEnd is null
     * @throws VehicleNotRentedException     in case the vehicle is not rented at all
     * @throws InvalidRentingPeriodException in case the rentalEnd is before the currently noted start date of rental or
     *                                       in case the Vehicle does not allow the passed period for rental, e.g. Caravans must be rented for at least a day
     *                                       and the driver tries to return them after an hour.
     */
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException, VehicleNotRentedException {
        if (rentalEnd == null) {
            throw new IllegalArgumentException("RentalEnd cannot be null");
        }

        if (this.rentStartTime == null) {
            throw new VehicleNotRentedException("Vehicle is not rented!");
        }

        if (rentalEnd.isBefore(this.rentStartTime)) {
            throw new InvalidRentingPeriodException("RentalEnd cannot be before rentStartTime");
        }
    }

    /**
     * Used to calculate potential rental price without the vehicle to be rented.
     * The calculation is based on the type of the Vehicle (Car/Caravan/Bicycle).
     *
     * @param startOfRent the beginning of the rental
     * @param endOfRent   the end of the rental
     * @return potential price for rent
     * @throws InvalidRentingPeriodException in case the vehicle cannot be rented for that period of time or
     *                                       the period is not valid (end date is before start date)
     */
    public abstract double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException;
}