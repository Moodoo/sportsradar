package com.example.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardCancelMatchTest {

    @Test
    void shouldCancelMatchAndRemoveItFromSummary() {
        Scoreboard scoreboard = new Scoreboard();

        UUID matchId = scoreboard.startMatch("Poland", "Germany");
        scoreboard.updateScore(matchId, 2, 1);

        assertEquals(1, scoreboard.getSummary().size());

        scoreboard.cancelMatch(matchId);

        assertTrue(scoreboard.getSummary().isEmpty());
    }

    @Test
    void shouldNotAllowScoreUpdateAfterMatchIsCancelled() {
        Scoreboard scoreboard = new Scoreboard();

        UUID matchId = scoreboard.startMatch("Poland", "Germany");

        scoreboard.cancelMatch(matchId);

        assertThrows(
                ScoreboardException.class,
                () -> scoreboard.updateScore(matchId, 3, 1)
        );
    }

    @Test
    void shouldNotAllowFinishingCancelledMatch() {
        Scoreboard scoreboard = new Scoreboard();

        UUID matchId = scoreboard.startMatch("Poland", "Germany");

        scoreboard.cancelMatch(matchId);

        assertThrows(
                ScoreboardException.class,
                () -> scoreboard.finishMatch(matchId)
        );
    }

    @Test
    void shouldHandleCancelledMatchLifecycle() {
        Scoreboard scoreboard = new Scoreboard();

        UUID matchId = scoreboard.startMatch("Poland", "Germany");

        // Match starts
        assertEquals(1, scoreboard.getSummary().size());

        // Score is updated
        scoreboard.updateScore(matchId, 2, 1);

        assertEquals(2, scoreboard.getSummary().get(0).homeScore());
        assertEquals(1, scoreboard.getSummary().get(0).awayScore());

        // Match is cancelled
        scoreboard.cancelMatch(matchId);

        // Cancelled match is no longer active
        assertTrue(scoreboard.getSummary().isEmpty());

        // It cannot be updated
        assertThrows(
                ScoreboardException.class,
                () -> scoreboard.updateScore(matchId, 3, 1)
        );

        // It cannot be finished
        assertThrows(
                ScoreboardException.class,
                () -> scoreboard.finishMatch(matchId)
        );
    }
}
