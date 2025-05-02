package bg.sofia.uni.fmi.mjt.poll.server.repository;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPollRepository implements PollRepository{
    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public synchronized int addPoll(Poll poll) {
        for (Map.Entry<Integer, Poll> entry : polls.entrySet()) {
            Poll existing = entry.getValue();
            if (existing.name().equals(poll.name()) &&
                    existing.options().keySet().equals(poll.options().keySet())) {
                return entry.getKey();
            }
        }

        int id = idGenerator.getAndIncrement();
        polls.put(id, poll);
        return id;
    }


    @Override
    public Poll getPoll(int pollId) {
        return polls.get(pollId);
    }

    @Override
    public Map<Integer, Poll> getAllPolls() {
        return Map.copyOf(polls);
    }

    @Override
    public void clearAllPolls() {
        this.polls.clear();
    }
}
