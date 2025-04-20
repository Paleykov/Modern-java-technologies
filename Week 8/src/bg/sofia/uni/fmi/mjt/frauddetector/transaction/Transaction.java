package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction (String transactionID, String accountID, double transactionAmount,
                           LocalDateTime transactionDate, String location, Channel channel){
    private static final String TRANSACTION_ATTRIBUTE_DELIMITER = ",";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Transaction of(String line) {
        final String[] tokens = line.split(TRANSACTION_ATTRIBUTE_DELIMITER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(tokens[3], formatter);

        return new Transaction(
                tokens[0],
                tokens[1],
                Double.parseDouble(tokens[2]),
                date,
                tokens[4],
                Channel.valueOf(tokens[5].trim().toUpperCase())
        );
    }
}
