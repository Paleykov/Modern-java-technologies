package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class ZScoreRule implements Rule{
    private double zScoreThreshold;
    private double weight;

    public ZScoreRule(double zScoreThreshold, double weight){
        this.zScoreThreshold = zScoreThreshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        // Mean (μ)
        double μ = transactions.stream()
                .mapToDouble(Transaction::transactionAmount)
                .average()
                .orElse(0.0);

        double variance = transactions.stream()
                .mapToDouble(t -> Math.pow(t.transactionAmount() - μ, 2))
                .average()
                .orElse(0.0);

        // Standard Deviation (σ)
        double σ = Math.sqrt(variance);
        if (σ == 0.0) {
            return false;
        }

        return transactions.stream()
                .mapToDouble(t -> Math.abs((t.transactionAmount() - μ) / σ))
                .anyMatch(z -> z >= zScoreThreshold);
    }

    @Override
    public double weight() {
        return this.weight;
    }
}
