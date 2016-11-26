package pl.tndsoft.cache.service.memorycache;

import pl.tndsoft.cache.service.Cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.*;

/**
 * @author Tomasz Jagiełło <jagiello.tomek@gmail.com>
 */
public class MemoryCache<K, V> implements Cache<K, V> {

    private final ConcurrentMap<K, CacheEntry<K, V>> map = new ConcurrentHashMap<>();

    private final SortedSet<CacheEntry<K, V>> cacheEntriesTreeSet = Collections.synchronizedSortedSet(new TreeSet<>());

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private long maxCapacity = -1;

    /**
     * Default Memory Cache without max capacity and automatic cleanup.
     */
    public MemoryCache() {
    }

    /**
     * Memory Cache without max capacity.
     *
     * @param cleanupDelayInMillis fixed rate to cleanup obsolete entries.
     */
    public MemoryCache(long cleanupDelayInMillis) {
        if (cleanupDelayInMillis > 0) {
            scheduledExecutorService.scheduleAtFixedRate(this::scheduleCleanup,
                    cleanupDelayInMillis, cleanupDelayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Memory Cache
     *
     * @param cleanupDelayInMillis fixed rate to cleanup obsolete entries.
     * @param maxCapacity          max capacity of cache
     */
    public MemoryCache(long cleanupDelayInMillis, long maxCapacity) {
        this(cleanupDelayInMillis);
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        CacheEntry<K, V> cacheEntry = map.get(key);
        if (cacheEntry != null) {
            cacheEntriesTreeSet.remove(cacheEntry);
        }
        CacheEntry<K, V> newCacheEntry = new CacheEntry<>(key, value, timeToLiveInMillis);
        map.put(key, newCacheEntry);
        cacheEntriesTreeSet.add(newCacheEntry);
        if (isMaxCapacityExceeded()) {
            scheduleCleanup();
        }
    }

    @Override
    public V get(K key) {
        CacheEntry<K, V> cacheEntry = map.get(key);
        if (cacheEntry == null) {
            return null;
        }
        if (cacheEntry.isExpired()) {
            cacheEntriesTreeSet.remove(cacheEntry);
            map.remove(key);
            return null;
        }
        return cacheEntry.getValue();
    }

    public void scheduleCleanup() {
        executorService.submit(this::cleanupCache);
    }

    private void cleanupCache() {
        Iterator<CacheEntry<K, V>> iterator = cacheEntriesTreeSet.iterator();
        while (iterator.hasNext()) {
            CacheEntry<K, V> next = iterator.next();
            if (next.isExpired() || isMaxCapacityExceeded()) {
                map.remove(next.getKey());
                iterator.remove();
            } else {
                break;
            }
        }
    }

    private boolean isMaxCapacityExceeded() {
        return maxCapacity > 0 && maxCapacity < cacheEntriesTreeSet.size();
    }

}
