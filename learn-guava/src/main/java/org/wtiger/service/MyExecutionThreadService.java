package org.wtiger.service;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

public class MyExecutionThreadService extends AbstractExecutionThreadService {
  private int counter = 0;

  @Override
  protected void run() throws Exception {
    System.out.println("MyExecutionThreadService started");
    while (isRunning()) {
      counter++;
    }
    System.out.println(String.format("MyExecutionThreadService counted %s times", counter));
  }
}
