package org.wtiger.learn.mockito;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@SuppressWarnings("unchecked")
class MockitoTest {
  //Using mockito-inline
  @Mock(answer = Answers.RETURNS_SMART_NULLS) //Gives more detailed information for NPEs, but final methods will return plain null.
  List<Object> listMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Nested
  class MocksAndStubsBase {
    @Test
    void mockAndDefaults() {
      assertEquals(0, listMock.size()); //Primitives and boxed result returns 0
      assertEquals(emptyList(), listMock.subList(0, 10)); //collections result returns empty collection
      assertNull(listMock.get(0)); //Otherwise returns null

      List<Object> listMockWithDefaultAnswer
              = Mockito.mock(List.class, withSettings().defaultAnswer(invocation -> invocation.getMock())); //Answers.RETURNS_SELF Use for builders
      assertSame(listMockWithDefaultAnswer, listMockWithDefaultAnswer.subList(0, 100)); //or default answer if possible (configured)
    }

    @Test
    void stubbing() {
      when(listMock.size()).thenReturn(100);
      assertEquals(100, listMock.size());

      when(listMock.remove(anyString())).thenReturn(true).thenReturn(false); //Chain. Last stub important
      assertTrue(listMock.remove("1"));
      assertFalse(listMock.remove("1"));
      assertFalse(listMock.remove("1"));

      when(listMock.isEmpty()).then(invocation -> ThreadLocalRandom.current().nextBoolean());
    }

    @Test
    void oneLiner() {
      List oneLineMock = when(mock(List.class).size()).thenReturn(10).getMock();

      assertEquals(10, oneLineMock.size());
    }

    @Test
    @Ignore
      //todo find stateless mock example
    void partialMockAndSpy() {
      List<Object> listMock = spy(ArrayList.class); //Implementation!

      doCallRealMethod().when(listMock).add(any()); //Non abstract method!
      when(listMock.size()).thenCallRealMethod();

      listMock.add("value");

      assertEquals(1, listMock.size());

      //We can use partial mocking for stateless classes
    }
  }

  @Nested
  class Spy {
    @Test
    void spy() {
      List<String> listSpy = Mockito.spy(new ArrayList<>());
      listSpy.add("value");
      assertEquals("value", listSpy.get(0));

      when(listSpy.add("value2")).thenCallRealMethod().thenThrow(new RuntimeException()); //Unexpected invocation
      assertEquals(2, listSpy.size()); //Surprise

      doCallRealMethod().doThrow(new RuntimeException()).when(listSpy).add("value3"); //Correct way
      assertEquals(2, listSpy.size());
    }

    @Test
    void delegating() {
      List<String> list = new ArrayList<>();
      list.add("value");

      when(listMock.get(anyInt())).then(AdditionalAnswers.delegatesTo(list));

      assertEquals(list.get(0), listMock.get(0));
    }

    @Test
    void customConfigSpy() {
      List<String> listSpy = Mockito.mock(ArrayList.class, withSettings().useConstructor(singleton("Value")).defaultAnswer(CALLS_REAL_METHODS));

      assertEquals("Value", listSpy.get(0));
    }
  }

  @Nested
  class ArgumentMatchers {
    // When you use matcher as an argument of the method, you should use matchers for all other arguments of the method.

    @Test
    void that() {
      listMock.add("value");

      verify(listMock).add(argThat(argument -> ((String) argument).length() == 5));
    }
  }

  @Nested
  class Verifying {
    @Test
    void verifyInOrder() {
      List listMock = mock(List.class);
      Queue queueMock = mock(Queue.class);

      listMock.add("String");
      listMock.get(0);
      queueMock.add("done");

      InOrder inOrder = Mockito.inOrder(listMock, queueMock); //Arguments order doesn't matter
      inOrder.verify(listMock).add("String");
      inOrder.verify(listMock).get(0);
      inOrder.verify(queueMock).add("done");
    }

    @Test
    void verifyNoMoreInteractions() {
      List listMock = mock(List.class);
      listMock.size();
      verify(listMock).size(); //Verified

      Mockito.verifyNoMoreInteractions(listMock); //Were no interactions except verified
      Mockito.verifyZeroInteractions(listMock); //Same
    }

    @Test
    void verifyWithTimeout() {
      new Thread(() -> {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        listMock.add("Value from kafka async");
        listMock.add("Value2 from kafka async");
        listMock.add("Value2 from kafka async");
      }).start();
      verify(listMock, timeout(1_000)).add("Value from kafka async");
      verify(listMock, timeout(1_000).times(2)).add("Value2 from kafka async");
    }
  }

  @Test
  void reset() {
    Mockito.reset(listMock); // is a code smell
  }

  @Nested
  class BDD {
    //import static org.mockito.BDDMockito.*;
    @Test
    void aliases() {
      //given
      given(listMock.size()).willReturn(100);

      //when
      int size = listMock.size();

      //then
      assertEquals(100, size);
    }

    @Test
    void verification() {
      //given
      given(listMock.size()).willReturn(100);

      //when
      int size = listMock.size();

      //then
      then(listMock).should(times(1)).size();
    }
  }

  public interface SomethingWithSize {
    int size();
  }

  public static class StringList extends ArrayList<Integer> {

  }

  @Nested
  class Settings {
    @Test
    void serializable() {
      List serializableMock = mock(ArrayList.class, withSettings().serializable());

      List<Object> list = new ArrayList<>();
      List<Object> serializableSpy = mock(ArrayList.class, withSettings()
              .spiedInstance(list)
              .defaultAnswer(CALLS_REAL_METHODS)
              .serializable());
    }

    @Test
    void extraInterfaces() {
      ArrayList mock = mock(ArrayList.class, withSettings().extraInterfaces(SomethingWithSize.class));
      SomethingWithSize byIface = (SomethingWithSize) mock; //Make possible to cast. Useful for legacy

      assertEquals(0, byIface.size());
    }

    @Test
    void returnMocks() {
      List<Integer> listMock = mock(StringList.class, RETURNS_MOCKS); //Only for the first level

      Stream<Integer> stream = listMock.stream();
      assertEquals(0, stream.count());
    }

    @Test
    void deepStub() {
      List<Integer> listMock = mock(StringList.class, RETURNS_DEEP_STUBS); //Same as RETURNS_MOCKS but goes deeper

      Iterator<Integer> iterator = listMock.iterator();
      Integer next = iterator.next();

      assertEquals(0, next);
    }
  }
}
