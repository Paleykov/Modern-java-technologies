package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;

public class Glovo implements GlovoApi {
    private ControlCenter controlCenter;

    private boolean isValidLocation(Location loc) {
        int x = loc.x();
        int y = loc.y();
        return x >= 0 && x < controlCenter.getLayout().length &&
                y >= 0 && y < controlCenter.getLayout()[0].length;
    }

    private void isValidClientAndRestaurant(MapEntity client, MapEntity restaurant) throws InvalidOrderException {
        if (client == null || restaurant == null) {
            throw new InvalidOrderException("Client or restaurant is null");
        }

        Location clientLocation = client.location();
        Location restaurantLocation = restaurant.location();

        if (!isValidLocation(clientLocation) || !isValidLocation(restaurantLocation)) {
            throw new InvalidOrderException("Client or restaurant location is outside map bounds");
        }

        if (client.type() != MapEntityType.CLIENT) {
            throw new InvalidOrderException("Provided client entity is not of type CLIENT");
        }

        if (restaurant.type() != MapEntityType.RESTAURANT) {
            throw new InvalidOrderException("Provided restaurant entity is not of type RESTAURANT");
        }
    }

    private Delivery buildDeliveryFromControlCenter(MapEntity client, MapEntity restaurant, String foodItem,
                                                    double maxPrice, int maxTime, ShippingMethod method)
            throws NoAvailableDeliveryGuyException {

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(
                restaurant.location(),
                client.location(),
                maxPrice,
                maxTime,
                method
        );

        if (info == null) {
            throw new NoAvailableDeliveryGuyException("No available delivery person meets the criteria.");
        }

        return new Delivery(
                client.location(),
                restaurant.location(),
                info.deliveryGuyLocation(),
                foodItem,
                info.price(),
                info.estimatedTime()
        );
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {

        isValidClientAndRestaurant(client, restaurant);
        return buildDeliveryFromControlCenter(client, restaurant, foodItem, -1, -1, ShippingMethod.CHEAPEST);
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {

        isValidClientAndRestaurant(client, restaurant);
        return buildDeliveryFromControlCenter(client, restaurant, foodItem, -1, -1, ShippingMethod.FASTEST);
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant, String foodItem, double maxPrice)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {

        isValidClientAndRestaurant(client, restaurant);
        return buildDeliveryFromControlCenter(client, restaurant, foodItem, maxPrice, -1, ShippingMethod.FASTEST);
    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant, String foodItem, int maxTime)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {

        isValidClientAndRestaurant(client, restaurant);
        return buildDeliveryFromControlCenter(client, restaurant, foodItem, -1, maxTime, ShippingMethod.CHEAPEST);
    }

}
