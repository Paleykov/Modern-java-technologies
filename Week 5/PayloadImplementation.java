package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class PayloadImplementation<T> implements Payload<T> {
    private final T payload;

    public PayloadImplementation(T payload) {
        this.payload = payload;
    }

    @Override
    public int getSize() {
        if (payload instanceof Collection<?> collection) {
            return collection.size();
        } else if (payload instanceof Map<?, ?> map) {
            return map.size();
        } else if (payload instanceof String str) {
            return str.length();
        } else if (payload instanceof Object[] arr) {
            return arr.length;
        }

        return 1;
    }

    @Override
    public T getPayload() {
        return payload;
    }
}
