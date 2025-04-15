package bg.sofia.uni.fmi.mjt.vehiclerent.main;

import bg.sofia.uni.fmi.mjt.vehiclerent.RentalService;
import bg.sofia.uni.fmi.mjt.vehiclerent.driver.AgeGroup;
import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.*;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.*;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws InvalidRentingPeriodException, VehicleAlreadyRentedException, VehicleNotRentedException {

        RentalService rentalService = new RentalService();
        LocalDateTime rentStart = LocalDateTime.of(2024, 10, 10, 0, 0, 0);
        Driver experiencedDriver = new Driver(AgeGroup.EXPERIENCED);
        Driver youngDriver = new Driver(AgeGroup.JUNIOR);

        Vehicle electricCar = new Car("1", "Tesla Model 3", FuelType.ELECTRIC, 4, 1000, 150, 10);
        rentalService.rentVehicle(experiencedDriver, electricCar, rentStart);
        double priceToPay = rentalService.returnVehicle(electricCar, rentStart.plusDays(5));
        System.out.println("Price for Tesla: " + priceToPay);

        rentalService.rentVehicle(youngDriver, electricCar, rentStart.plusDays(7));

        Vehicle dieselCar = new Car("2", "Toyota Auris", FuelType.DIESEL, 4, 500, 80, 5);
        rentalService.rentVehicle(experiencedDriver, dieselCar, rentStart);
        priceToPay = rentalService.returnVehicle(dieselCar, rentStart.plusDays(5));
        System.out.println("Price for Toyota: " + priceToPay);
    }
}
