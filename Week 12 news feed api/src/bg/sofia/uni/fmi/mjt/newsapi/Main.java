package bg.sofia.uni.fmi.mjt.newsapi;

import bg.sofia.uni.fmi.mjt.newsapi.exceptions.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsapi.result.Article;
import bg.sofia.uni.fmi.mjt.newsapi.result.NewsResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {
    private static final String API_KEY = "hehehehe";

    public static void main(String[] args) {
        NewsApiClient client = new NewsApiClient(API_KEY);

        QueryBuilder query = new QueryBuilder()
                .keywords("+java")
                .language("en")
                .page(1);

        CompletableFuture<Void> future = client.searchAsync(query)
                .thenAccept(result -> {
                    System.out.println("Status: " + result.status());
                    System.out.println("Total results: " + result.totalResults());
                    System.out.println("Articles on this page: " + result.articles().size());
                    System.out.println();

                    for (Article article : result.articles()) {
                        System.out.println("=== Article ===");
                        System.out.println(article);
                        System.out.println();
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Async query failed: " + ex.getMessage());
                    return null;
                });

        future.join();
    }
}
