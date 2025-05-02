package bg.sofia.uni.fmi.mjt.newsapi.result;

import java.util.List;

public record NewsResult(
        String status,
        int totalResults,
        List<Article> articles
) {}
