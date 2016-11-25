package pl.tndsoft.cache.service.memorycache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tomasz Jagiełło <jagiello.tomek@gmail.com>
 */
@RunWith(SpringRunner.class)
public class MemoryCacheTest {

  private MemoryCache<String, String> memoryCache;

  private Instant clockTime;

  /**
   * Instantiation basing fields, set fixed clock on current time
   */
  @Before
  public void init() {
    memoryCache = new MemoryCache<>();
    clockTime = Instant.now();
    setMockClock(clockTime);
  }

  /**
   * Test simple put and get behavior.
   */
  @Test
  public void cacheTest() {
    memoryCache.put("key1", "value1", 100000L);
    assertThat(memoryCache.get("key1")).isEqualTo("value1");
    assertThat(memoryCache.get("key2")).isNull();
  }

  /**
   * Test behavior for replacing entry.
   */
  @Test
  public void cacheTestSameKey() {
    memoryCache.put("key1", "value1", 100000L);
    memoryCache.put("key1", "value2", 100000L);
    assertThat(memoryCache.get("key1")).isEqualTo("value2");
    assertThat(memoryCache.get("key2")).isNull();
  }

  /**
   * Test removing after timeout.
   */
  @Test
  public void cacheTestAfterTimeout() {
    memoryCache.put("key1", "value1", 100L);
    memoryCache.put("key2", "value2", 1000L);
    setMockClock(clockTime.plusMillis(500L));
    assertThat(memoryCache.get("key1")).isNull();
    assertThat(memoryCache.get("key2")).isEqualTo("value2");
  }

  /**
   * Test clean up method.
   */
  @Test
  public void cacheCleanUpTest() {
    // prepare data
    memoryCache.put("key1", "value1", 100L);
    memoryCache.put("key2", "value2", 1200L);
    memoryCache.put("key3", "value3", 800L);
    memoryCache.put("key4", "value4", 50L);
    memoryCache.put("key5", "value5", 1000L);

    // invoke on mock time
    setMockClock(clockTime.plusMillis(900L));
    ReflectionTestUtils.invokeMethod(memoryCache, "cleanupCache");

    // check collection sizes
    assertThat((Map) ReflectionTestUtils.getField(memoryCache, "map")).hasSize(2);
    assertThat((Collection) ReflectionTestUtils.getField(memoryCache, "cacheEntriesTreeSet")).hasSize(2);

    // check keys in cache
    assertThat(memoryCache.get("key1")).isNull();
    assertThat(memoryCache.get("key2")).isEqualTo("value2");
    assertThat(memoryCache.get("key3")).isNull();
    assertThat(memoryCache.get("key4")).isNull();
    assertThat(memoryCache.get("key5")).isEqualTo("value5");
  }

  /**
   * Test max capacity.
   */
  @Test
  public void testMaxCapacity() {
    memoryCache = new MemoryCache<>(-1, 3);
    // replace executor service to run immediately for testing purposes
    ReflectionTestUtils.setField(memoryCache, "executorService", new ExecutorServiceAdapter(Runnable::run));
    memoryCache.put("key1", "value1", 100L);
    memoryCache.put("key2", "value2", 200L);
    memoryCache.put("key3", "value3", 400L);
    memoryCache.put("key4", "value4", 300L);
    memoryCache.put("key5", "value5", 500L);
    assertThat(memoryCache.get("key1")).isNull();
    assertThat(memoryCache.get("key2")).isNull();
    assertThat(memoryCache.get("key3")).isEqualTo("value3");
    assertThat(memoryCache.get("key4")).isEqualTo("value4");
    assertThat(memoryCache.get("key5")).isEqualTo("value5");
  }

  /**
   * Sets mock clock on specified time.
   */
  private void setMockClock(Instant instant) {
    ReflectionTestUtils.setField(CacheEntry.class, "clock", Clock.fixed(instant, ZoneId.systemDefault()));
  }

}