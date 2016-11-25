package pl.tndsoft.cache.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.tndsoft.cache.service.memorycache.MemoryCache;

import java.util.Map;

/**
 * @author Tomasz Jagiełło <jagiello.tomek@gmail.com>
 */
@Controller
@RequestMapping("/memory/cache")
public class MemoryCacheController {

  @Autowired
  private MemoryCache<String, Map<String, Object>> memoryCache;

  @GetMapping("/{key}")
  public @ResponseBody Map<String, Object> get(@PathVariable("key") String key) {
    return memoryCache.get(key);
  }

  @PutMapping("/{key}")
  public @ResponseBody void put(@PathVariable("key") String key, @RequestBody Map<String, Object> value,
                  @RequestParam("timeToLiveInMillis") long timeToLiveInMillis) {
    memoryCache.put(key, value, timeToLiveInMillis);
  }

}
