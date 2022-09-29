package org.wtiger.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@DisplayName("Test JUnit5 possibilities")
@TestMethodOrder(MethodOrderer.Random.class)
@Execution(ExecutionMode.CONCURRENT)
class JUnit5CommonTest {
  @Test
  @Tag("banana")
  void testInfo(TestInfo testInfo) {
    System.out.println(testInfo.getTestClass().get());
    
    //Важно. Старайтесь чаще использовать assertThat или assertEquals, и реже использовать assertTrue. assertTrue - не информативен.
    Assertions.assertTrue(testInfo.getTags().contains("banana"));
  }

  @Nested
  class Asserts {
    //Now a lot of 3rd side assertions excluded from JUnit and can be added through maven test scope dependencies

    @Test
    @DisplayName("assertThrows case with NPE")
    void assertTrows() {
      Assertions.assertThrows(NullPointerException.class, () -> ((Object) null).toString());
    }

    @Test
    @Tag("slow")
    void assertTimeoutFail() {
      //Test will be executed longer than 10_000 ms
      Assertions.assertTimeout(Duration.ofMillis(10), () -> Thread.sleep(10_000));
    }

    @Test
    void assertTimeoutPreemptivelyFail() {
      //Test will be executed much less than 10_000 ms
      //Important to now, task will be executed in a different thread.  This behavior can lead to undesirable side effects
      Assertions.assertTimeoutPreemptively(Duration.ofMillis(10), () -> Thread.sleep(10_000));
    }

    @Test
    // Test will be executed much less than 10_000 ms
    // Task will be executed in the main thread and could be interrupted from another thread.
    // If applied on class then not applied to lifecycle methods
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void timeoutWithAnnotation() throws InterruptedException {
      Thread.sleep(10_000);
    }

    @Test
    void assertAll() {
      Assertions.assertAll("All invocations are executed independently",
          () -> Assertions.assertTrue(false, "War is Peace"),
          () -> Assertions.assertFalse(true, "Freedom is Slavery"));
    }
  }

  @Nested
  @DisplayName("Assumptions - WTF? And some of parametrized tests")
  class AssumesAndParametrized {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 2})
    void palindromes(int nonNegativeNumber) {
      Assertions.assertTrue(nonNegativeNumber / -1 <= 0);
      Assumptions.assumeTrue(nonNegativeNumber != 0);
      Assertions.assertTrue(-1 / nonNegativeNumber <= 0);
    }

    @ParameterizedTest(name = "[{index}] toString() works fine with [{0}]")
    @MethodSource("org.wtiger.junit5.JUnit5CommonTest#methodSource")
      // Or:
      // 1) Method shorten name
      // 2) Same name of method
      // 3) ArgumentProvider with @ArgumentSource
    void withMethodSource(Object args) {
      Assertions.assertDoesNotThrow(args::toString);
    }
  }

  @Nested
  class EnablingDisabling {
    @Disabled
    @Test
    void disabledTest() {

    }

    @Test
    @DisabledOnOs(OS.MAC)
    @EnabledIfSystemProperty(named = "KAFKA_HOME", matches = "*")
    @DisabledIfEnvironmentVariable(named = "UNO", matches = "dos")
    void disabledOnMacOS() {

    }

    @Test
    @EnabledOnOs({OS.MAC, OS.WINDOWS})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9})
    void enabledOnMacOS() {

    }
  }

  @RepeatedTest(5)
  void repetitions() {
    Assertions.assertTrue(ThreadLocalRandom.current().nextDouble(0, Double.MAX_VALUE) >= 0);
  }

  private static Stream methodSource() {
    return Stream.of("", 1, null);
  }
}
