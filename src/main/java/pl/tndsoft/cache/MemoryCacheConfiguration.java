package pl.tndsoft.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.tndsoft.cache.service.memorycache.MemoryCache;

import java.util.Map;

@Configuration
public class MemoryCacheConfiguration {

	@Value("${cache.memory.maxCapacity:-1}")
	private long maxCapacity;

	@Value("${cache.memory.cleanupDelayInMillis}")
	private long cleanupDelayInMillis;

	@Bean
	public MemoryCache<String, Map<String, Object>> memoryCache() {
		return new MemoryCache<>(cleanupDelayInMillis, maxCapacity);
	}
}
