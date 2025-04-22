package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer.Consumer;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer.Producer;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer.Task;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelSentimentAnalyzer implements SentimentAnalyzerAPI{
    private int workersCount;
    private Set<String> stopWords;
    private Map<String, SentimentScore> sentimentLexicon;

    public ParallelSentimentAnalyzer(int workersCount, Set<String> stopWords, Map<String, SentimentScore> sentimentLexicon) {
        this.workersCount=workersCount;
        this.stopWords=stopWords;
        this.sentimentLexicon=sentimentLexicon;
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... inputs) throws SentimentAnalysisException {
        BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
        ConcurrentMap<String, SentimentScore> results = new ConcurrentHashMap<>();

        List<Thread> producers = new ArrayList<>();
        for(AnalyzerInput input : inputs){
            Thread producerThread = new Thread(new Producer(input, taskQueue));
            producers.add(producerThread);
            producerThread.start();
        }

        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < workersCount; i++) {
            Thread consumerThread = new Thread(new Consumer(taskQueue, stopWords, sentimentLexicon, results));
            consumers.add(consumerThread);
            consumerThread.start();
        }

        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                throw new SentimentAnalysisException("Producer interrupted", e);
            }
        }

        for (int i = 0; i < workersCount; i++) {
            try {
                taskQueue.put(Task.KILL_YOURSELF);
            } catch (InterruptedException e) {
                throw new SentimentAnalysisException("Interrupted while sending command to kill oneself", e);
            }
        }

        for (Thread consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                throw new SentimentAnalysisException("Consumer interrupted", e);
            }
        }

        return results;
    }
}
