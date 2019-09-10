package org.wtiger.eventbus;

import com.google.common.eventbus.EventBus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class EventBusTest {

  private static final EventBus EVENT_BUS = new EventBus("Bus-" + UUID.randomUUID().toString());

  @Test
  void registerAndPost() {
    EVENT_BUS.register(new MyEventListener("Listener1"));
    EVENT_BUS.register(new MyEventListener("Listener2"));

    EVENT_BUS.post(new MyEvent("Event1"));
  }

  @Test
  void registerDeadEvent() {
    EVENT_BUS.register(new MyEventListener("Listener1"));
    EVENT_BUS.register(new MyEventListener("Listener2"));

    EVENT_BUS.post("Banana");
  }

  @Test
  void unregister() {
    MyEventListener listenerTounregister = new MyEventListener("Listener2");
    EVENT_BUS.register(listenerTounregister);

    EVENT_BUS.register(new MyEventListener("Listener1"));
    EVENT_BUS.unregister(listenerTounregister);

    EVENT_BUS.post("Banana");
  }
}