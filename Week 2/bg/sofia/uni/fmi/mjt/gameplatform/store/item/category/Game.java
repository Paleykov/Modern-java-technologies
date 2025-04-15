package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Game implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private String genre;
    private List<Double> ratings;

    public Game(String title, BigDecimal price, LocalDateTime releaseDate, String genre){
        this.title = title;
        this.price = price;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.ratings = new ArrayList<>();
    }

    @Override
    public StoreItem copy() {
        Game copy = new Game(this.title, this.price, this.releaseDate, this.genre);
        for (double r : ratings) {
            copy.rate(r);
        }
        return copy;
    }

    @Override
    public String getTitle() {

        return this.title;
    }

    public String getGenre() {
        return genre;
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

    public void setGenre(String genre) {

        this.genre = genre;
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
        ratings.add(rating);
    }
}
