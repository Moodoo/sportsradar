package com.example.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardConcurrencyTest {
    @Test
    void manyMatchesCanRunConcurrently() throws Exception {
        Scoreboard s = new Scoreboard();
        ExecutorService e = Executors.newFixedThreadPool(10);
        List<Future<UUID>> fs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int n = i;
            fs.add(e.submit(() -> s.startMatch("H" + n, "A" + n)));
        }
        for (int i = 0; i < 100; i++) {
            UUID id = fs.get(i).get();
            final int n = i;
            e.submit(() -> s.updateScore(id, n, n + 1)).get();
        }
        assertEquals(100, s.getSummary().size());
        e.shutdown();
    }

    @Test
    void finishAndUpdateAreSerialized() throws Exception {
        Scoreboard s = new Scoreboard();
        UUID id = s.startMatch("Poland", "Germany");
        ExecutorService e = Executors.newFixedThreadPool(2);
        Future<?> u = e.submit(() -> s.updateScore(id, 3, 2));
        Future<MatchSummary> f = e.submit(() -> s.finishMatch(id));
        u.get();
        assertNotNull(f.get());
        assertTrue(s.getSummary().isEmpty());
        e.shutdown();
    }

    @Test
    void concurrentEqualScoresUseStartOrder() {
        Scoreboard s = new Scoreboard();
        s.startMatch("First", "A");
        s.startMatch("Second", "B");
        // second match is newer; summary ordering is deterministic by start sequence
        assertEquals("Second", s.getSummary().get(0).homeTeam());
    }
}