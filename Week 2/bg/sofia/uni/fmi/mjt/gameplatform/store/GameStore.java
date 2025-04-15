package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PromoCodes {
    public static final String VAN40 = "VAN40";
    public static final String YO100 = "100YO";

    public static final BigDecimal VAN40_DISCOUNT = new BigDecimal("0.40");
    public static final BigDecimal YO100_DISCOUNT = BigDecimal.ONE;
}

public class GameStore implements StoreAPI {
    private StoreItem[] availableItems;

    public GameStore(StoreItem[] availableItems) {
        if (availableItems == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }

        this.availableItems = new StoreItem[availableItems.length];

        for (int i = 0; i < availableItems.length; i++) {
            this.availableItems[i] = availableItems[i].copy();
        }
    }

    @Override
    public StoreItem[] findItemByFilters(ItemFilter[] itemFilters) {
        List<StoreItem> filtered = new ArrayList<>();

        for(StoreItem item : availableItems){
            boolean found = true;

            for(ItemFilter filter : itemFilters){
                if(!filter.matches(item)){
                    found = false;
                    break;
                }
            }

            if (found) {
                filtered.add(item);
            }
        }

        return filtered.toArray(new StoreItem[0]);
    }

    @Override
    public void applyDiscount(String promoCode) {
        BigDecimal discount;

        switch (promoCode) {
            case PromoCodes.VAN40:
                discount = PromoCodes.VAN40_DISCOUNT;
                break;
            case PromoCodes.YO100:
                discount = PromoCodes.YO100_DISCOUNT;
                break;
            default:
                discount = BigDecimal.valueOf(0);
                return;
        }


        for (StoreItem item : this.availableItems) {
            BigDecimal originalPrice = item.getPrice();
            System.out.println(originalPrice);
            BigDecimal discountedPrice = originalPrice.subtract(originalPrice.multiply(discount));
            System.out.println(discountedPrice);
            item.setPrice(discountedPrice);
            // item.getPrice();
        }
    }

    public StoreItem[] getAvailableItems() {
        return Arrays.copyOf(availableItems, availableItems.length);
    }

    @Override
    public boolean rateItem(StoreItem item, int rating) {
        if(rating > 0 && rating <= 5){
            item.rate(rating);
            return true;
        }

        return false;
    }
}
