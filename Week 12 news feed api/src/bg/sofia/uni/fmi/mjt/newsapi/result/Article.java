package bg.sofia.uni.fmi.mjt.newsapi.result;

public record Article(
        Source source,
        String author,
        String title,
        String description,
        String url,
        String urlToImage,
        String publishedAt,
        String content
) {}
