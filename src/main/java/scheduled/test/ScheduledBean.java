package scheduled.test;

import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
@Requires(env = "xyz")
public class ScheduledBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledBean.class);
  private final DummyService dummyService;

  public ScheduledBean(DummyService dummyService) {
    LOGGER.info("ScheduledBean constructed");
    this.dummyService = dummyService;
  }

  @Scheduled(fixedRate = "2s")
  public void scheduledJob() {
    LOGGER.info(String.format("Scheduled job running for %s",  dummyService.getDummy() ));
  }
}
