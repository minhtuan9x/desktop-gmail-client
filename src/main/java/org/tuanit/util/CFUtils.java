package org.tuanit.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class CFUtils {

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return CompletableFuture.runAsync(runnable, executorService).whenComplete((unused, throwable) -> executorService.shutdown());
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return CompletableFuture.supplyAsync(supplier, executorService).whenComplete((unused, throwable) -> executorService.shutdown());
    }
}
