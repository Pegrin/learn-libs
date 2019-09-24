package org.wtiger.service;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

public class MyScheduledService extends AbstractScheduledService {
  @Override
  protected void runOneIteration() throws Exception {
    System.out.println("MyScheduledService executed once");
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.HOURS);
  }
}
