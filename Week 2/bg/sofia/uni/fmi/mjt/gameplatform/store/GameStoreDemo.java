package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.GameStore;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.Game;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.PriceItemFilter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GameStoreDemo {

    public static void main(String[] args) {

        Game game1 = new Game("Cyberpunk 2077", new BigDecimal("60.00"), LocalDateTime.of(2020, 12, 10, 0, 0), "RPG");
        Game game2 = new Game("Stardew Valley", new BigDecimal("15.00"), LocalDateTime.of(2016, 2, 26, 0, 0), "Farming");
        Game game3 = new Game("Hollow Knight", new BigDecimal("10.00"), LocalDateTime.of(2017, 2, 24, 0, 0), "Metroidvania");

        StoreItem[] items = new StoreItem[]{game1, game2, game3};
        GameStore store = new GameStore(items);

        System.out.println("=== ALL GAMES BEFORE DISCOUNTS ===");
        printItems(store);

        System.out.println("\n=== GAMES BETWEEN $10 AND $20 ===");
        ItemFilter priceFilter = new PriceItemFilter(new BigDecimal("10.00"), new BigDecimal("20.00"));
        StoreItem[] filtered = store.findItemByFilters(new ItemFilter[]{priceFilter});
        printItems(filtered);

        System.out.println("\n=== APPLYING VAN40 DISCOUNT ===");
        store.applyDiscount("VAN40");
        printItems(store);


        System.out.println("\n=== RATING GAMES ===");
        StoreItem[] storeItems = store.getAvailableItems();
        store.rateItem(storeItems[0], 5);
        store.rateItem(storeItems[0], 4);
        store.rateItem(storeItems[1], 3);
        store.rateItem(storeItems[2], 6);

        printRatings(store);
    }

    private static void printItems(GameStore store) {
        StoreItem[] items = store.getAvailableItems();
        for (StoreItem item : items) {
            System.out.printf("%s - $%s - Released: %s\n",
                    item.getTitle(),
                    item.getPrice(),
                    item.getReleaseDate().toLocalDate());
        }
    }

    private static void printItems(StoreItem[] items) {
        for (StoreItem item : items) {
            System.out.printf("%s - $%s - Released: %s\n",
                    item.getTitle(),
                    item.getPrice(),
                    item.getReleaseDate().toLocalDate());
        }
    }

    private static void printRatings(GameStore store) {
        StoreItem[] items = store.getAvailableItems();
        for (StoreItem item : items) {
            System.out.printf("%s - Rating: %.2f\n", item.getTitle(), item.getRating());
        }
    }
}

