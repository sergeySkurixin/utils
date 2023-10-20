package rs.skurikhin.utils.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class AsyncUtils {
    public static final Object NO_RESULT = new Object();

    public static <T> CompletableFuture<Object> anyOf(CompletableFuture<T>... futures) {
        return anyOf(List.of(futures));
    }

    /**
     * @return any of successful non null result. Mute exceptions
     */
    public static <T> CompletableFuture<Object> anyOf(List<CompletableFuture<T>> futures) {
        return CompletableFuture.anyOf(futures.stream()
                .map(f -> f.exceptionally(th -> null))
                .map(f -> f.thenCompose(r -> {
                    if (r == null) return new CompletableFuture<T>();
                    return CompletableFuture.completedFuture(r);
                }))
                .toArray(CompletableFuture[]::new)
        );
    }

    private AsyncUtils() {
    }
}
