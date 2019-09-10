package org.wtiger.eventbus;

public class MyEvent {
  private final String name;

  public MyEvent(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }
}
