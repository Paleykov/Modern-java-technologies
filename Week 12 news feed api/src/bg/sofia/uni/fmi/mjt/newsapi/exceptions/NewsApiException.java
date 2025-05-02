package bg.sofia.uni.fmi.mjt.newsapi.exceptions;

public class NewsApiException extends Exception{

    public NewsApiException(String message) {
        super(message);
    }

    public NewsApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public NewsApiException(Throwable cause) {
        super(cause);
    }
}
