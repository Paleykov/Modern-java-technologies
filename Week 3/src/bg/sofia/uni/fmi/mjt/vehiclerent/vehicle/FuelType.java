package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

public enum FuelType {
    PETROL(5),
    DIESEL(7),
    ELECTRIC(3);

    private final int dailyFee;

    FuelType(int dailyFee) {
        this.dailyFee = dailyFee;
    }

    public int getDailyFee() {
        return dailyFee;
    }
}

