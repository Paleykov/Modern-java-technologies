package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

public class TransactionAnalyzerImpl implements TransactionAnalyzer{
    private List<Rule> rules;
    private List<Transaction> transactions;

    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules){
        this.rules = rules;
        this.transactions = new BufferedReader(reader)
                .lines()
                .skip(1)
                .map(Transaction::of)
                .toList();
    }

    @Override
    public List<Transaction> allTransactions() {
        return this.transactions;
    }

    @Override
    public List<String> allAccountIDs() {
        return this.transactions.stream()
                .map(Transaction::accountID)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        return this.transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::channel,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }


    @Override
    public double amountSpentByUser(String accountID) {
        if(accountID==null){
            throw new IllegalArgumentException("Account cannot be null");
        }
        return this.transactions.stream()
                .filter(t -> t.accountID().equals(accountID))
                .mapToDouble(Transaction::transactionAmount)
                .sum();
    }

    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if(accountId==null){
            throw new IllegalArgumentException("Account cannot be null");
        }

        return this.transactions.stream()
                .filter(t -> t.accountID().equals(accountId))
                .collect(Collectors.toList());
    }

    @Override
    public double accountRating(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        List<Transaction> userTransactions = allTransactionsByUser(accountId);

        return Math.min(rules.stream()
                .filter(rule -> rule.applicable(userTransactions))
                .mapToDouble(Rule::weight)
                .sum(), 1);
    }

    @Override
    public SortedMap<String, Double> accountsRisk() {
        List<String> accountIds = allAccountIDs();

        Map<String, Double> riskMap = new HashMap<>();
        for (String id : accountIds) {
            riskMap.put(id, accountRating(id));
        }

        Comparator<String> byRiskDescending = (id1, id2) -> {
            int cmp = Double.compare(riskMap.get(id2), riskMap.get(id1));
            if (cmp == 0) {
                return id1.compareTo(id2);
            }
            return cmp;
        };

        SortedMap<String, Double> sortedByRisk = new TreeMap<>(byRiskDescending);
        sortedByRisk.putAll(riskMap);

        return sortedByRisk;
    }

}
