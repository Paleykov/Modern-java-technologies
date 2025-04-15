package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameBundle implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private List<Double> ratings;
    private Game[] games;

    public GameBundle(String title, BigDecimal price, LocalDateTime releaseDate, Game[] games){
        this.title = title;
        this.price = price;
        this.releaseDate = releaseDate;
        this.games = games;
        this.ratings = new ArrayList<>();
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

        Game[] copiedGames = new Game[this.games.length];
        for (int i = 0; i < this.games.length; i++) {
            copiedGames[i] = (Game) this.games[i].copy();
        }

        GameBundle copiedBundle = new GameBundle(this.title, this.price, this.releaseDate, copiedGames);

        for (double r : this.ratings) {
            copiedBundle.rate(r);
        }

        return copiedBundle;
    }

}
