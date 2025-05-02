package bg.sofia.uni.fmi.mjt.poll.server.model;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public record Poll (String name, Map<String, AtomicInteger> options){}
