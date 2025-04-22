package bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.SentimentScore;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class Consumer implements Runnable{
    private final BlockingQueue<Task> taskQueue;
    private final Set<String> stopWords;
    private final Map<String, SentimentScore> sentimentLexicon;
    private final ConcurrentMap<String, SentimentScore> results;

    public Consumer(BlockingQueue<Task> taskQueue,
                    Set<String> stopWords,
                    Map<String, SentimentScore> sentimentLexicon,
                    ConcurrentMap<String, SentimentScore> results) {
        this.taskQueue = taskQueue;
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
        this.results = results;
    }

    /*
        1) toLower() - знаете защо
        2) Премахваме стоп думите от текста
        3) Премахваме всякаква пунктуация, табулации и т.н., трябва да останат само думи, разделени с whitespace.
        4) За всяка дума от текста извличаме "оценка" от лексикона, ако думата липсва - игнорираме.

        Оценката се смята като усредним всички "оценки на думи" (става и само да ги сумираме,
        тестовете ни ще проверяват само дали оценката е положителна, неутрална или отрицателна).
     */

    @Override
    public void run() {
        try {
            while (true) {
                Task task = taskQueue.take();
                if (task == Task.KILL_YOURSELF) break;

                String cleaned = task.text().toLowerCase();
                int score = computeScore(cleaned);
                results.put(task.inputID(), SentimentScore.fromScore(score));
            }
        } catch (InterruptedException e) {}
    }

    private int computeScore(String text) {
        return Arrays.stream(text.split("\\s+"))
                .filter(w -> !stopWords.contains(w))
                .mapToInt(word -> {
                        SentimentScore score = sentimentLexicon.get(word);
                        return (score == null) ? 0 : score.getScore();
                    })
                .sum();
    }
}
