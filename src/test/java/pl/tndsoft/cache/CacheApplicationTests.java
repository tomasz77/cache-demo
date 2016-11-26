package pl.tndsoft.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheApplicationTests {

    public static final String JSON_TEST_VALUE = "{\"test\":\"1\"}";

    @LocalServerPort
    private long port;

    private RestTemplate testRestTemplate = new RestTemplate();

    /**
     * Test if spring context loads.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * Test if REST methods put and get works.
     */
    @Test
    public void memoryCacheControllerTest() {
        Map<String, Object> value1 = new LinkedHashMap<>();
        value1.put("test", "1");
        testRestTemplate.put("http://localhost:" + port + "/memory/cache/key1?timeToLiveInMillis=1000000", value1);
        String result = testRestTemplate.getForObject("http://localhost:" + port + "/memory/cache/key1", String.class);
        String result2 = testRestTemplate.getForObject("http://localhost:" + port + "/memory/cache/key2", String.class);
        assertThat(result).isEqualTo(JSON_TEST_VALUE);
        assertThat(result2).isNull();
    }

}
