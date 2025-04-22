package bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions;

public class SentimentAnalysisException extends Exception {

    public SentimentAnalysisException(String message) {
        super(message);
    }

    public SentimentAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public SentimentAnalysisException(Throwable cause) {
        super(cause);
    }
}
