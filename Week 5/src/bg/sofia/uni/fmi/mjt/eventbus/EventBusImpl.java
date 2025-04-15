package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.*;

public class EventBusImpl implements EventBus {
    private final Map<Class<?>, List<Subscriber<?>>> subscribersByType = new HashMap<>();
    private final List<Event<?>> eventLogs = new ArrayList<>();

    public EventBusImpl(Map<Class<?>, List<Subscriber<?>>> subscribersByType, List<Event<?>> eventLogs) {
        this.subscribersByType.putAll(subscribersByType);
        this.eventLogs.addAll(eventLogs);
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if(subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }

        if(eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        List<Subscriber<?>> subscribers = subscribersByType.get(eventType);
        if (subscribers == null) {
            subscribers = new ArrayList<>();
            subscribersByType.put(eventType, subscribers);
        }

        subscribers.add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber) throws MissingSubscriptionException {
        if(subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }

        if(eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        List<Subscriber<?>> subscribers = subscribersByType.get(eventType);
        if (subscribers == null) {
            throw new MissingSubscriptionException("Subscriber not found for this event type");
        }

        subscribers.remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Class<?> eventClass = event.getClass();
        List<Subscriber<?>> subscribers = subscribersByType.get(eventClass);

        if (subscribers != null) {
            for (Subscriber<?> sub : subscribers) {
                @SuppressWarnings("unchecked")
                Subscriber<T> typedSub = (Subscriber<T>) sub;
                typedSub.onEvent(event);
            }
        }

        eventLogs.add(event);
    }

    @Override
    public void clear() {
        subscribersByType.clear();
        eventLogs.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (from == null) {
            throw new IllegalArgumentException("Start timestamp cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("End timestamp cannot be null");
        }
        if (from.equals(to)) {
            return Collections.emptyList();
        }

        List<Event<?>> result = new ArrayList<>();

        for (Event<?> event : eventLogs) {
            if (eventType.isAssignableFrom(event.getClass())) {
                Instant timestamp = event.getTimestamp();
                if ((timestamp.equals(from) || timestamp.isAfter(from)) &&
                        timestamp.isBefore(to)) {
                    result.add(event);
                }
            }
        }

        return result;
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        List<Subscriber<?>> subscribers = subscribersByType.get(eventType);

        if (subscribers == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(subscribers);
    }
}
