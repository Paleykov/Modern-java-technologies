package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.PathFinder;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;

import java.util.ArrayList;
import java.util.List;

public class ControlCenter implements ControlCenterApi {
    private MapEntity[][] layout;
    private final PathFinder pathFinder;

    public ControlCenter(MapEntity[][] layout) {
        this.layout = layout;
        this.pathFinder = new PathFinder(layout);
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation, double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        List<MapEntity> deliveryGuys = findAllDeliveryGuys();
        DeliveryInfo bestDelivery = null;

        for (MapEntity deliveryGuy : deliveryGuys) {
            Location guyLocation = deliveryGuy.location();
            DeliveryType deliveryType = getDeliveryType(deliveryGuy.type());

            int pricePerKm = deliveryType.getPricePerKilometer();
            int timePerKm = deliveryType.getTimePerKilometer();

            int toRestaurant = this.pathFinder.findDistance(guyLocation, restaurantLocation);
            int toClient = this.pathFinder.findDistance(restaurantLocation, clientLocation);

            if (toRestaurant == -1 || toClient == -1) continue;

            int totalDistance = toRestaurant + toClient;
            double price = totalDistance * pricePerKm;
            int time = totalDistance * timePerKm;

            if ((maxPrice != -1 && price > maxPrice) || (maxTime != -1 && time > maxTime)) {
                continue;
            }

            if (bestDelivery == null ||
                    (shippingMethod == ShippingMethod.CHEAPEST && price < bestDelivery.price()) ||
                    (shippingMethod == ShippingMethod.FASTEST && time < bestDelivery.estimatedTime())) {
                DeliveryInfo current = new DeliveryInfo(guyLocation, price, time, deliveryType);
                bestDelivery = current;
            }
        }

        return bestDelivery;
    }

    private DeliveryType getDeliveryType(MapEntityType type) {
        return switch (type) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> throw new IllegalArgumentException("Not a delivery guy: " + type);
        };
    }

    private List<MapEntity> findAllDeliveryGuys() {
        List<MapEntity> result = new ArrayList<>();

        for (MapEntity[] row : layout) {
            for (MapEntity entity : row) {
                if (entity.type() == MapEntityType.DELIVERY_GUY_CAR || entity.type() == MapEntityType.DELIVERY_GUY_BIKE) {
                    result.add(entity);
                }
            }
        }

        return result;
    }

    @Override
    public MapEntity[][] getLayout() {
        return this.layout;
    }
}
