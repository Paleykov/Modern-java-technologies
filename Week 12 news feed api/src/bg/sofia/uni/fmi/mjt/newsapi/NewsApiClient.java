package bg.sofia.uni.fmi.mjt.newsapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsapi.result.NewsResult;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class NewsApiClient {

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public NewsApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().create();
    }

    public NewsResult search(QueryBuilder query) throws NewsApiException {
        try {
            String uri = buildUri(query);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Authorization", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new NewsApiException("Failed with status code: " + response.statusCode() + "\n" + response.body());
            }

            return gson.fromJson(response.body(), NewsResult.class);

        } catch (IOException | InterruptedException e) {
            throw new NewsApiException("Error while sending request", e);
        }
    }

    public CompletableFuture<NewsResult> searchAsync(QueryBuilder query) {
        String uri = buildUri(query);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", apiKey)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new CompletionException(new NewsApiException(
                                "Failed with status code: " + response.statusCode() + "\n" + response.body()));
                    }
                    return gson.fromJson(response.body(), NewsResult.class);
                });
    }

    private String buildUri(QueryBuilder query) {
        Map<String, String> params = query.buildParams();
        String endpoint = query.requiresTopHeadlines()
                ? "https://newsapi.org/v2/top-headlines"
                : "https://newsapi.org/v2/everything";

        StringJoiner sj = new StringJoiner("&", endpoint + "?", "");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sj.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return sj.toString();
    }

    public String buildUriDebug(QueryBuilder query) {
        return buildUri(query);
    }
}
