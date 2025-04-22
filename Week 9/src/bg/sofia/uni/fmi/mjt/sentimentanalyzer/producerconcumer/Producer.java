package bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.AnalyzerInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final AnalyzerInput input;
    private final BlockingQueue<Task> taskQueue;

    public Producer(AnalyzerInput input, BlockingQueue<Task> taskQueue) {
        this.input = input;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(input.inputReader())) {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append(" ");
            }

            String fullText = text.toString();
            taskQueue.put(new Task(input.inputID(), fullText));
        } catch (IOException | InterruptedException e) {}
    }
}

