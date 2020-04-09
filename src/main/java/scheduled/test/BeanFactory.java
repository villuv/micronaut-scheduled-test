package scheduled.test;

import io.micronaut.context.annotation.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Factory
public class BeanFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(BeanFactory.class);

  @Singleton
  public DummyService dummyService() {
    LOGGER.info("Factory creates DummyService");
    return new DummyService();
  }

  // Comment this method out to make it work, it even magically injects DummyService into constructor
  /*
  @Singleton
  public ScheduledBean scheduledBean(DummyService dummyService) {
    LOGGER.info("Factory creates ScheduledBean");
    return new ScheduledBean(dummyService);
  }
  */
}
