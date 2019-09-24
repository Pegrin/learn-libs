package org.wtiger.service;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MyServicesTest {
  @Test
  void idleService() throws InterruptedException {
    ServiceManager serviceManager = new ServiceManager(Collections.singleton(new MyIdleService()));
    serviceManager.startAsync();
    serviceManager.awaitHealthy();

    assertTrue(serviceManager.isHealthy());

    serviceManager.stopAsync();
    serviceManager.awaitStopped();
  }

  @Test
  void executionThreadService() {
    MyExecutionThreadService service = new MyExecutionThreadService();
    ServiceManager serviceManager = new ServiceManager(Collections.singleton(service));
    serviceManager.startAsync();
    serviceManager.awaitHealthy();

    ImmutableMultimap<Service.State, Service> servicesByState = serviceManager.servicesByState();
    assertSame(service, servicesByState.get(Service.State.RUNNING).toArray()[0]);

    serviceManager.stopAsync();
    serviceManager.awaitStopped();
  }

  @Test
  void scheduledService() {
    MyScheduledService service = new MyScheduledService();
    ServiceManager serviceManager = new ServiceManager(Collections.singleton(service));
    serviceManager.startAsync();
    serviceManager.awaitHealthy();

    long serviceStartTime = serviceManager.startupTimes().get(service);
    System.out.println(String.format("Service started in %s millis", serviceStartTime));


    serviceManager.stopAsync();
    serviceManager.awaitStopped(); //Optional
  }
}