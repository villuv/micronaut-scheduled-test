# Weirdness with @Scheduled

`scheduled.test.ScheduledBean` has a `@Sheduled` method. There are different behaviours depending on some conditions, some of them are weird, some of them fail.

Created with Micronaut 1.3.4 using `mn create-app -b maven`. No additional dependencies.

Running using `java -jar target/scheduled-test-0.1.jar`

Java version:
```
openjdk version "11.0.5" 2019-10-15
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.5+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.5+10, mixed mode)
```

Classes used:
* `BeanFactory` - a `@Factory` bean factory
* `ScheduledBean` - a class having a `@Scheduled scheduledJob()` method, running periodically
* `DummyService` - a singleton dependency for `ScheduledBean`, created by `BeanFactory`, not used in some cases

## Case 1
Branch 'master'

### Conditions:
* `ScheduledBean` is does not have class-level annotations
* `ScheduledBean` does not have a factory method
* `ScheduledBean` depends on `DummyService` bean, uses constructor injection.

### Result:
* Factory creates single instance of `DummyService`
* `ScheduledBean` is created, `DummyService` injected into constructor
* `scheduledJob` method is being scheduled, a new instance of `ScheduledBean` is created for every run.

### My expectations:
* As `ScheduledBean` is not a bean (no annotations, no factory), it shouldn't be instantiated and method shouldn't be scheduled

## Case 2
Branch 'case-2'

### Conditions:
* `ScheduledBean` has `@Singleton @Requires(env = "xyz")` annotations
* `ScheduledBean` does not have a factory method
* `ScheduledBean` depends on `DummyService` bean, uses constructor injection.

### Result:

Running wihtout `xyz` environment
* `ScheduledBean` is not created
* `scheduledJob` is not scheduled

Running with `xyz` environment
*  A single instance of `ScheduledBean` is created
*  A single instance of `DummyService` is created
* `scheduledJob` is scheduled

### My expectations:
* Works as expected

## Case 3
Branch 'case-3'

### Conditions:
* `ScheduledBean` has no class-level annotations
* `ScheduledBean` is created by factory
* `ScheduledBean` does not have any dependencies

### Result:
Exception:
```
12:33:18.920 [pool-1-thread-1] ERROR i.m.s.DefaultTaskExceptionHandler - Error invoking scheduled task Multiple possible bean candidates found: [scheduled.test.ScheduledBean, scheduled.test.ScheduledBean]
io.micronaut.context.exceptions.NonUniqueBeanException: Multiple possible bean candidates found: [scheduled.test.ScheduledBean, scheduled.test.ScheduledBean]
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidate(DefaultBeanContext.java:1796)
	at io.micronaut.context.DefaultApplicationContext.findConcreteCandidate(DefaultApplicationContext.java:403)
	at io.micronaut.context.DefaultBeanContext.lastChanceResolve(DefaultBeanContext.java:2384)
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidateNoCache(DefaultBeanContext.java:2307)
	at io.micronaut.context.DefaultBeanContext.lambda$findConcreteCandidate$57(DefaultBeanContext.java:2250)
	at io.micronaut.core.util.clhm.ConcurrentLinkedHashMap.lambda$compute$0(ConcurrentLinkedHashMap.java:721)
	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1705)
	at io.micronaut.core.util.clhm.ConcurrentLinkedHashMap.compute(ConcurrentLinkedHashMap.java:733)
	at io.micronaut.core.util.clhm.ConcurrentLinkedHashMap.computeIfAbsent(ConcurrentLinkedHashMap.java:710)
	at io.micronaut.context.DefaultBeanContext.findConcreteCandidate(DefaultBeanContext.java:2249)
	at io.micronaut.context.DefaultBeanContext.getBeanInternal(DefaultBeanContext.java:2038)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:618)
	at io.micronaut.scheduling.processor.ScheduledMethodProcessor.lambda$process$5(ScheduledMethodProcessor.java:123)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)
```
This is repeated on every scheduled interval

### My expectations:
* An single instance of `ScheduledBean` is created by factory
* Scheduling should work
