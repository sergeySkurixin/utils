package rs.skurikhin.utils.async;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;


class AsyncUtilsTest {
    private static final int AWAIT_SECONDS = 3;
    private static final ScheduledExecutorService THREAD_POOL = Executors.newSingleThreadScheduledExecutor();

    @AfterAll
    public static void tearDown() {
        THREAD_POOL.shutdown();
    }

    @Test
    void name() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Integer> f1 = new CompletableFuture<>();
        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        CompletableFuture<Integer> f3 = new CompletableFuture<>();

        CompletableFuture<Object> res = AsyncUtils.anyOf(List.of(
                new CompletableFuture<>(),
                new CompletableFuture<>(),
                f1,
                f2,
                f3,
                new CompletableFuture<>()
        ));

        THREAD_POOL.schedule(() -> {
            System.out.println("!!1 complete 1");
            return f1.completeExceptionally(new RuntimeException());
        }, 100, TimeUnit.MILLISECONDS);
        THREAD_POOL.schedule(() -> {
            System.out.println("!!1 complete 2");
            return f2.complete(null);
        }, 300, TimeUnit.MILLISECONDS);
        THREAD_POOL.schedule(() -> {
            System.out.println("!!1 complete 3");
            return f3.complete(123);
        }, 500, TimeUnit.MILLISECONDS);

        System.out.println("!!! Blocking, waiting any of results");
        System.out.println("!!1 res: " + res.get(AWAIT_SECONDS, TimeUnit.SECONDS));

    }
}