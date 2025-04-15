package bg.sofia.uni.fmi.mjt.vehiclerent.driver;

public record Driver(AgeGroup ageGroup) {
    public Driver {
        if (ageGroup == null) {
            throw new IllegalArgumentException("AgeGroup must not be null.");
        }
    }
}
