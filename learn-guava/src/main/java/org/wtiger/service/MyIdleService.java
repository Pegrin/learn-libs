package org.wtiger.service;

import com.google.common.util.concurrent.AbstractIdleService;

public class MyIdleService extends AbstractIdleService {
  @Override
  protected void startUp() throws Exception {
    System.out.println("MyIdleService has been started");
  }

  @Override
  protected void shutDown() throws Exception {
    System.out.println("MyIdleService has been stopped");
  }
}
