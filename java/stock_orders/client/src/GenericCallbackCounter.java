package client;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GenericCallbackCounter {

    private static final ConcurrentMap<String, AtomicLong> callMap = new ConcurrentHashMap<String, AtomicLong>();
    private static final ConcurrentMap<String, AtomicLong> successMap = new ConcurrentHashMap<String, AtomicLong>();
    private static final ConcurrentMap<String, AtomicLong> errorMap = new ConcurrentHashMap<String, AtomicLong>();

    public static long call(String name) {
        // fast path
        AtomicLong counter = callMap.get(name);
        if (counter != null)
            return counter.incrementAndGet();

        // first time
        AtomicLong newCounter = new AtomicLong();
        counter = callMap.putIfAbsent(name, newCounter);

        return (counter == null ? newCounter.incrementAndGet() : counter.incrementAndGet());
    }

    public static long success(String name) {
        // fast path
        AtomicLong counter = successMap.get(name);
        if (counter != null)
            return counter.incrementAndGet();

        // first time
        AtomicLong newCounter = new AtomicLong();
        counter = successMap.putIfAbsent(name, newCounter);

        return (counter == null ? newCounter.incrementAndGet() : counter.incrementAndGet());
    }

    public static long error(String name) {
        // fast path
        AtomicLong counter = errorMap.get(name);
        if (counter != null)
            return counter.incrementAndGet();

        // first time
        AtomicLong newCounter = new AtomicLong();
        counter = errorMap.putIfAbsent(name, newCounter);

        return (counter == null ? newCounter.incrementAndGet() : counter.incrementAndGet());
    }

    public static long getCallCount(String name) {
        AtomicLong counter = callMap.get(name);
        if (counter != null) {
            return counter.get();
        } else {
            return 0l;
        }
    }

    public static long getSuccessCount(String name) {
        AtomicLong counter = successMap.get(name);
        if (counter != null) {
            return counter.get();
        } else {
            return 0l;
        }
    }

    public static long getErrorCount(String name) {
        AtomicLong counter = errorMap.get(name);
        if (counter != null) {
            return counter.get();
        } else {
            return 0l;
        }
    }

    public static void printProcedureResults(String procedureName) {
        System.out.println("  " + procedureName);
        System.out.println("        calls: " + getCallCount(procedureName));
        System.out.println("      commits: " + getSuccessCount(procedureName));
        System.out.println("    rollbacks: " + getErrorCount(procedureName));
    }

}
