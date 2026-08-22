package eu.bilch.scale;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();
  private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);

  @GetMapping("/api/greeting")
  public String greeting(@RequestParam(defaultValue = "World") String name) {
    counter.incrementAndGet();
    String response = template.formatted(name);
    logger.debug("Returning greeting #{}: {}", counter, response);
    return response;
  }
}