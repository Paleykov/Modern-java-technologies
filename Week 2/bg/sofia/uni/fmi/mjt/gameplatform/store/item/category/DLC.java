package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

// import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.Game;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DLC implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private List<Double> ratings;
    private Game og_game;


    public DLC(String title, BigDecimal price, LocalDateTime releaseDate, Game game){
        this.title = title;
        this.price = price;
        this.releaseDate = releaseDate;
        this.ratings = new ArrayList<>();
        this.og_game = game;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public double getRating() {
        if (ratings.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (double r : this.ratings) {
            sum += r;
        }

        return sum / ratings.size();
    }

    @Override
    public LocalDateTime getReleaseDate() {
        return this.releaseDate;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public void rate(double rating) {
        this.ratings.add(rating);
    }

    @Override
    public StoreItem copy() {
        Game baseGameCopy = (Game) this.og_game.copy();

        DLC copiedDLC = new DLC(this.title, this.price, this.releaseDate, baseGameCopy);

        for (double r : this.ratings) {
            copiedDLC.rate(r);
        }

        return copiedDLC;
    }
}
