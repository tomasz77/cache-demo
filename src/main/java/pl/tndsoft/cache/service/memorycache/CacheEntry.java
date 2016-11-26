package pl.tndsoft.cache.service.memorycache;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Tomasz Jagiełło <jagiello.tomek@gmail.com>
 */
public class CacheEntry<K, V> implements Comparable<CacheEntry<K, V>> {

    private static Clock clock = Clock.systemDefaultZone();

    private final K key;

    private final V value;

    private final LocalDateTime expirationTime;

    public CacheEntry(K key, V value, long timeToLiveInMillis) {
        this.key = key;
        this.value = value;
        expirationTime = LocalDateTime.now(clock).plus(timeToLiveInMillis, ChronoUnit.MILLIS);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return expirationTime.isBefore(LocalDateTime.now(clock));
    }

    @Override
    public int compareTo(CacheEntry<K, V> cacheEntry) {
        return expirationTime.compareTo(cacheEntry.expirationTime);
    }
}
