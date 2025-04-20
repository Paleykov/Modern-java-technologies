package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.List;

public class FrequencyRule implements Rule {
    private int transactionCountThreshold;
    private TemporalAmount timeWindow;
    private double weight;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight){
        this.transactionCountThreshold=transactionCountThreshold;
        this.timeWindow = timeWindow;
        this.weight=weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        var latestTransaction = transactions.stream()
                .map(Transaction::transactionDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow(); //Fixes red squiggles since it's var and is not yet evaluated???

        var startOfWindow = latestTransaction.minus(timeWindow);

        long count = transactions.stream()
                .filter(t -> {
                    var time = t.transactionDate();
                    return !time.isBefore(startOfWindow) && !time.isAfter(latestTransaction);
                })
                .count();

        return count >= transactionCountThreshold;
    }



    // I decided I wanted to write a function that checks it for all transcactions at once
    /*
    public boolean applicableForAllTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }
        var sortedByTime = transactions.stream().sorted(Comparator.comparing(Transaction::transactionDate)).toList();

        for (int i = 0; i < sortedByTime.size(); i++) {
            Transaction base = sortedByTime.get(i);
            LocalDateTime windowStart = base.transactionDate().minus(timeWindow);

            long count = 0;

            for (int j = i; j >= 0; j--) {
                LocalDateTime Date = sortedByTime.get(j).transactionDate();
                if (!Date.isBefore(windowStart)) {
                    count++;
                } else {
                    break;
                }
            }

            if (count >= transactionCountThreshold) {
                System.out.printf(
                        "[FrequencyRule] Triggered for account ID: %s at transaction time: %s (Found %d transactions in the past %s)%n",
                        base.accountID(),
                        base.transactionDate(),
                        count,
                        timeWindow.toString()
                );
                return true;
            }
        }

        return false;
    }
    */

    @Override
    public double weight() {
        return this.weight;
    }
}
