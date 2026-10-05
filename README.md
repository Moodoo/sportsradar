# Thread-Safe Scoreboard

## Model
A match moves through `IN_PROGRESS -> FINISHED` or `IN_PROGRESS -> CANCELLED`.

`updateScore()` changes only the current score. `finishMatch()` ends the match and returns its final result. `getSummary()` contains only matches still in progress.

## Why threads?
A real scoreboard receives updates for different matches concurrently. The implementation therefore protects shared state against race conditions.

## Synchronization
`ConcurrentHashMap` provides safe concurrent collection access. `AtomicLong` gives every match a deterministic start sequence. `ReentrantReadWriteLock` makes compound operations atomic.

Write lock protects:
- startMatch
- updateScore
- finishMatch
- cancelMatch

Read lock protects:
- getSummary

This matters because `get -> check status -> update` must be one atomic operation. Otherwise one thread could update a match while another thread finishes it.

## Example concurrent usage
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> scoreboard.updateScore(match1, 2, 1));
executor.submit(() -> scoreboard.updateScore(match2, 4, 3));
executor.submit(() -> scoreboard.finishMatch(match3));
executor.submit(scoreboard::getSummary);
```

## Ordering
1. total score descending
2. if tied, most recently started first

## Additional feature
`cancelMatch(UUID)` marks an active match as CANCELLED. It is introduced as a separate Git commit:
`git commit -m "Add match cancellation feature"`

## Tests
The project includes JUnit tests for the assignment example and concurrent tests using `ExecutorService` and `Future`.

Run:
```bash
mvn clean test
```

## Complexity
start/update/finish/cancel: O(1)
getSummary: O(n log n) because of sorting.
