package ca.applin.demo;

import jdk.incubator.concurrent.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * <h2><a href="https://openjdk.org/jeps/429">JEP 429</a>: Scoped Values (Incubator)</h2>
 * Introduce scoped values, which enable the sharing of immutable data within and across threads. They are preferred to
 * thread-local variables, especially when using large numbers of virtual threads. This is an incubating API.
 * <p></p>
 * <h2><a href="https://openjdk.org/jeps/436">JEP 436</a>: Virtual Threads (Second Preview)</h2>
 * A small number of API changes described by JEP 425 were made permanent in JDK 19 and thus are not proposed
 * for preview here. These changes were made permanent because they involve functionality that is broadly useful and
 * is not specific to virtual threads. They consist of new methods in Thread (join(Duration), sleep(Duration), and threadId()),
 * new methods in Future (to examine task state and result), and the change to make ExecutorService extend AutoCloseable.
 * <p></p>
 * The degradations to ThreadGroup described in JEP 425 were made permanent in JDK 19.
 *  <p></p>
 *  <h2><a href="https://openjdk.org/jeps/437">JEP 437</a>: Structured Concurrency (Second Incubator)</h2>
 *  The only change in the re-incubated API is that StructuredTaskScope is updated to support the inheritance of
 *  scoped values (JEP 429) by threads created in a task scope. This streamlines the sharing of immutable data across threads.
 */
public class Concurrent {

    static final Random RANDOM = new Random();
    static final ScopedValue<Integer> SCOPED_VALUE = ScopedValue.newInstance();
    static int unsafe = 0;

    static Set<Integer> safe = Collections.synchronizedSet(new HashSet<>());

    public void doWithValue() {
        if (RANDOM.nextInt(2) % 2 == 0) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                scope.fork(this::doWork);
            }
        } else {
            doWork();
        }
    }

    private int doWork() {
        unsafe += 1;
        try {
            Thread.sleep(RANDOM.nextInt(2000));
        } catch (InterruptedException e) {}
        System.out.println("VALUE: " + SCOPED_VALUE.get());
        safe.add(SCOPED_VALUE.get());
        return SCOPED_VALUE.get();
    }

    public static void main(String[] args) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i ->
                executor.submit(() -> ScopedValue.where(SCOPED_VALUE, i)
                                                 .run(new Concurrent()::doWithValue)));
        }
        Thread.sleep(Duration.ofSeconds(3));
        System.out.println("UNSAFE VALUE: " + unsafe);
        System.out.println("SAFE VALUE: " + safe.size());
    }

}