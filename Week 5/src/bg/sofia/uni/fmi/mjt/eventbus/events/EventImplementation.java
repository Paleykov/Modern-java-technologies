package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.time.Instant;

public class EventImplementation<T extends Payload<?>> implements Event<T> {
    private final Instant timestamp;
    private final int priority;
    private final String source;
    private final T payload;

    public EventImplementation(Instant timestamp, Integer priority, String source, T payload) {
        this.timestamp = timestamp;
        this.priority = priority;
        this.source = source;
        this.payload = payload;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public T getPayload() {
        return this.payload;
    }
}
