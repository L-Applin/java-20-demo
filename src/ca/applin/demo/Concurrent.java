package ca.applin.demo;

import jdk.incubator.concurrent.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Concurrent {

    static final ScopedValue<Integer> SCOPED_VALUE = ScopedValue.newInstance();
    static Set<Integer> ints = new HashSet<>();

    static int unsafe = 0;

    public int i;
    public Concurrent(int i) {
        this.i = i;
    }

    public String doWithValue() {
        if (i % 2 == 0) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                Future<String> future = scope.fork(this::doWork);
                scope.join();
                return future.get();
            } catch (Exception e) {
                return "ERROR!!!";
            }
        } else {
            return doWork();
        }
    }

    private String doWork() {
        unsafe += 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String str = "VALUE: " + SCOPED_VALUE.get();
        System.out.println(str);
        ints.add(SCOPED_VALUE.get());
        return str;
    }

    public static void main(String[] args) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000)
                    .mapToObj(Concurrent::new)
                    .forEach(instance -> executor.submit(() ->
                            ScopedValue.where(SCOPED_VALUE, instance.i)
                                       .run(instance::doWithValue)));
        }
        Thread.sleep(2000);
        System.out.println("UNSAFE VALUE: " + unsafe);
        System.out.println("SAFE VALUE: " + ints.size());
    }

}