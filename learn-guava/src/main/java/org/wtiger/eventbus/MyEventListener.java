package org.wtiger.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class MyEventListener {
  private final String name;

  public MyEventListener(String name) {
    this.name = name;
  }

  @Subscribe
  @AllowConcurrentEvents
  public void processEvent(MyEvent event) {
    System.out.println(String.format("Event '%s' initialized by listener '%s'.", event.name(), name));
  }

  @Subscribe
  public void processDeadEvent(DeadEvent deadEvent) {
    System.out.println(String.format("Listener '%s' received a dead event '%s' from source '%s'", name, deadEvent.getEvent(), deadEvent.getSource()));
  }
}
