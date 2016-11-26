package pl.tndsoft.cache.service;

/**
 * @author Tomasz Jagiełło <jagiello.tomek@gmail.com>
 */
public interface Cache<K, V> {

    void put(K key, V value, long timeToLiveInMillis);

    V get(K key);

}
