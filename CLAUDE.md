# Java Examples - Head First Java 3rd Edition Study Journal

## Goal
Prepare for Senior Software Engineer interviews by deeply learning Java concepts from Head First Java (3rd Edition).
Target: Cover all 17 chapters in 1 week (2026-08-31 to 2026-09-07).

## Background
- User is a Java developer currently at a junior level
- Has worked with shallow Java basics; wants intermediate/senior-level understanding
- Studying Head First Java 3rd Edition as primary reference

## Structure
Each chapter has:
1. **Example class** — demonstrates all key concepts from that chapter with inline explanations
2. **Exercise** — one standalone program to solve; marked with `// TODO: EXERCISE` in the class

All classes have a `main` method and can be run independently.

## Chapter Map

| Chapter | Topic | File |
|---------|-------|------|
| 01 | Breaking the Surface | `chapter01-breaking-the-surface/Chapter01Main.java` |
| 02 | A Trip to Objectville | `chapter02-objectville/Chapter02Main.java` |
| 03 | Know Your Variables | `chapter03-variables/Chapter03Main.java` |
| 04 | How Objects Behave | `chapter04-object-behavior/Chapter04Main.java` |
| 05 | Extra-Strength Methods | `chapter05-methods/Chapter05Main.java` |
| 06 | Using the Java Library | `chapter06-java-library/Chapter06Main.java` |
| 07 | Better Living in Objectville | `chapter07-inheritance/Chapter07Main.java` |
| 08 | Serious Polymorphism | `chapter08-polymorphism/Chapter08Main.java` |
| 09 | Life and Death of an Object | `chapter09-constructors/Chapter09Main.java` |
| 10 | Numbers and Statics | `chapter10-numbers-statics/Chapter10Main.java` |
| 11 | Exception Handling | `chapter11-exceptions/Chapter11Main.java` |
| 12 | Getting a GUI | `chapter12-gui/Chapter12Main.java` |
| 13 | Work on Your Swing | `chapter13-swing/Chapter13Main.java` |
| 14 | Saving Objects | `chapter14-serialization/Chapter14Main.java` |
| 15 | Make a Connection | `chapter15-networking/Chapter15Main.java` |
| 16 | Data Structures | `chapter16-collections/Chapter16Main.java` |
| 17 | Lambda and Streams | `chapter17-lambdas-streams/Chapter17Main.java` |
| 18 | Testing Like a Professional (JUnit 5 + Mockito) | `chapter18-testing/Chapter18Main.java` |

Chapter 18 isn't from Head First Java — it and the senior-level "Problem 2/3"
exercises bolted onto chapters 07, 08, 10, 13, 14, 15, 16 (SOLID, Factory/
Adapter, sealed classes + pattern matching, static memory leaks, injection
vulnerabilities, hand-rolled JSON via reflection, transaction semantics,
Semaphore/CountDownLatch/deadlocks, equals()/hashCode() contract) were added
2026-09-21 to cover senior-engineer interview topics the book's 17 chapters
don't reach. See each `ChapterNNExercise.java`'s doc comment for the full
problem statement.

## Conversation Log

### 2026-08-31 — Session 1
**User request:** Create a GitHub repo `Java-examples` with one directory per chapter of Head First Java 3rd edition. Each chapter should have example classes explaining concepts + one exercise. One class per chapter, independently runnable. 1 week to complete.

**Actions taken:**
- Created GitHub repo: https://github.com/shyamala-venkat/Java-examples
- Created all 17 chapter directories with example Java classes
- Each class covers key concepts with comments + one exercise problem

**How to run any chapter (01-17):**
```bash
cd chapter01-breaking-the-surface
javac Chapter01Main.java
java Chapter01Main
```

**How to run chapter 18 (needs JUnit 5 + Mockito, not plain javac):**
```bash
cd chapter18-testing
mvn test
```
Or open `chapter18-testing/` as a Maven project in IntelliJ and run tests from
the gutter icons. `pom.xml` there is scoped to this chapter only — every other
chapter stays zero-dependency, single-file `javac`.

### 2026-09-21 — Session 2
**User request:** Assess whether the existing chapters/exercises align with real
senior Java engineer work, and add any missing must-know concepts as exercises.

**Assessment:** existing content was already unusually senior-tilted (Big-O
notes, LSP/CME/RCE/backpressure callouts, production caveats throughout).
Confirmed gaps: automated testing (entirely absent), concurrency primitives
beyond executors/futures, equals/hashCode contract, Factory/Adapter patterns,
sealed classes + pattern matching, static-field memory leaks, hand-rolled JSON
via reflection, injection vulnerabilities, SOLID refactor practice, in-memory
transaction semantics.

**Actions taken:**
- Added "Problem 2" (and "Problem 3" on ch08/ch14) skeleton exercises, in the
  same TODO-driven style as the existing problems, to chapters 07, 08, 10, 13,
  14, 15, 16 — see the Chapter Map note above for the full topic list.
- Created new `chapter18-testing/` (JUnit 5 + Mockito) with a concept file, an
  exercise file, and a `pom.xml` scoped to just that chapter.
- Verified: all 17 javac-based chapters still compile clean as one batch;
  `mvn test` in chapter18 resolves dependencies and passes (concept file's
  real tests all pass; the exercise file's 4 stub tests pass vacuously as
  expected, since their bodies are still TODOs for the learner to fill in).

## Notes for Follow-up Questions
- Ask follow-up questions on any class — the classes are designed to be starting points for discussion
- Each `// TODO: EXERCISE` block is your hands-on practice problem
- Concepts build chapter by chapter — later chapters assume earlier concepts
