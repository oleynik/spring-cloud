package ua.infinity.courses.springmicroservices.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    public String hardcodedResponse(Throwable throwable) {
        return "fallback-response";
    }

    @GetMapping("/sample-api-a")
    @Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
    public String sampleApiA() {
        logger.info("Sample API Alpha call received");
        ResponseEntity<String> forEntity = new RestTemplate()
                .getForEntity("http://localhost:8080/some-dummy-url", String.class);
        return forEntity.getBody();
    }

    @GetMapping("/sample-api-b")
    @CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    public String sampleApiB() {
        logger.info("Sample API Bravo call received");
        return "Sample API Bravo";
    }

    @GetMapping("/sample-api-c")
    @RateLimiter(name = "default")
    public String sampleApiC() {
        logger.info("Sample API Charlie call received");
        return "Sample API Charlie";
    }

    @GetMapping("/sample-api-d")
    @Bulkhead(name = "default")
    public String sampleApiD() {
        logger.info("Sample API Delta call received");
        return "Sample API Delta";
    }
}
