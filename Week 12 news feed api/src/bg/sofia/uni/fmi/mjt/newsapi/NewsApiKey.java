package bg.sofia.uni.fmi.mjt.newsapi;

public class NewsApiKey {
    private final String apiKey;

    public NewsApiKey(String apiKey) {
        if(apiKey == null){
            throw new IllegalArgumentException("Api key cannot be null");
        }

        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}
