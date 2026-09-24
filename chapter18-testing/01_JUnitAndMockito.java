/**
 * CONCEPT: Automated Testing — JUnit 5 + Mockito
 *
 * No senior engineer ships code without tests, and "how would you test this?"
 * is one of the most common interview questions there is. This is the one
 * chapter in this repo you CANNOT just `javac` + `java` — JUnit tests are run
 * by a test runner, not a main() method.
 *
 * How to run this chapter:
 *   cd chapter18-testing
 *   mvn test
 *   (or: in IntelliJ, click the green gutter icon next to any @Test method or class)
 *
 * Why this chapter breaks the "single file, zero dependencies" pattern every
 * other chapter follows: JUnit and Mockito are real libraries that need real
 * dependency management — that's exactly the problem Maven/Gradle solve, and
 * every production Java codebase you'll touch as a senior engineer uses one
 * of them. pom.xml in this directory wires up just enough Maven to run this
 * one chapter; it doesn't affect any other chapter's plain-javac workflow.
 */
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JUnitAndMockito {

    // ============ Part 1: JUnit 5 basics — testing a pure function ============
    static class Calculator {
        int divide(int a, int b) {
            if (b == 0) throw new ArithmeticException("divide by zero");
            return a / b;
        }
    }

    @Nested
    @DisplayName("Calculator")
    class CalculatorTests {
        Calculator calc;

        @BeforeEach   // runs before EACH @Test — fresh instance, no state leaks between tests
        void setUp() { calc = new Calculator(); }

        @Test
        @DisplayName("divides two positive numbers")
        void dividesPositiveNumbers() {
            assertEquals(5, calc.divide(10, 2));   // assertEquals(expected, actual)
        }

        @Test
        void throwsOnDivideByZero() {
            // assertThrows verifies the exception type AND that it's actually thrown —
            // a test that just calls divide(10,0) and expects nothing would silently
            // pass even if the method stopped throwing entirely.
            assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
        }

        @ParameterizedTest      // runs the SAME test body once per value — no copy-paste tests
        @ValueSource(ints = {1, 2, 5, 100})
        void divideByOneReturnsSameNumber(int n) {
            assertEquals(n, calc.divide(n, 1));
        }
    }

    // ============ Part 2: Mockito — testing a class through a real dependency ============
    // UserRepository would normally hit a database. In a unit test we don't want a real
    // DB — we want to test UserService's LOGIC in isolation. Mockito fakes the dependency
    // so we can script its behavior and assert how it was called.
    interface UserRepository {
        String findEmailById(int userId);
    }

    static class UserService {
        private final UserRepository repo;
        UserService(UserRepository repo) { this.repo = repo; }   // constructor injection —
        // this is WHY dependency injection matters: the real repo and a mock are
        // interchangeable here, because UserService only knows about the interface.

        String getWelcomeMessage(int userId) {
            String email = repo.findEmailById(userId);
            if (email == null) throw new IllegalArgumentException("No such user: " + userId);
            return "Welcome, " + email + "!";
        }
    }

    @ExtendWith(MockitoExtension.class)   // enables @Mock injection for this nested class
    @Nested
    @DisplayName("UserService (with mocked repository)")
    class UserServiceTests {

        @Mock UserRepository mockRepo;   // Mockito creates a fake UserRepository —
        // no real implementation, no database, no network call.

        @Test
        void buildsWelcomeMessageFromRepoEmail() {
            when(mockRepo.findEmailById(42)).thenReturn("alice@example.com");  // script the fake

            UserService service = new UserService(mockRepo);
            String result = service.getWelcomeMessage(42);

            assertEquals("Welcome, alice@example.com!", result);
            verify(mockRepo).findEmailById(42);   // prove the collaborator was actually
            // called — this is what distinguishes a mock from a plain stub: you can
            // assert on INTERACTIONS, not just return values.
        }

        @Test
        void throwsWhenRepoReturnsNull() {
            when(mockRepo.findEmailById(999)).thenReturn(null);

            UserService service = new UserService(mockRepo);
            assertThrows(IllegalArgumentException.class, () -> service.getWelcomeMessage(999));
        }

        @Test
        void neverCallsRepoTwiceForOneLookup() {
            when(mockRepo.findEmailById(1)).thenReturn("x@y.com");
            new UserService(mockRepo).getWelcomeMessage(1);
            verify(mockRepo, times(1)).findEmailById(1);   // times(1) is verify()'s default,
            // but spelling it out documents intent — catches an accidental double-call bug.
        }
    }
}
