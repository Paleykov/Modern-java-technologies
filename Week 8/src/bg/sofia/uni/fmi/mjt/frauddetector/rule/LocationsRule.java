package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class LocationsRule implements Rule{
    private int threshold;
    private double weight;

    public LocationsRule(int threshold, double weight){
        this.threshold = threshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        return transactions.stream()
                .map(Transaction::location)
                .collect(Collectors.toSet())
                .size() >= this.threshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
