package bg.sofia.uni.fmi.mjt.vehiclerent.exception;

public class VehicleAlreadyRentedException extends Exception {

    public VehicleAlreadyRentedException(String message) {
        super(message);
    }

    public VehicleAlreadyRentedException(String message, Throwable cause) {
        super(message, cause);
    }

    public VehicleAlreadyRentedException(Throwable cause) {
        super(cause);
    }
}

