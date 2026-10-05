package com.example.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {
    @Test
    void assignmentExample() {
        Scoreboard s = new Scoreboard();
        UUID m = s.startMatch("Mexico", "Canada"), sp = s.startMatch("Spain", "Brazil"),
                g = s.startMatch("Germany", "France"), u = s.startMatch("Uruguay", "Italy"),
                a = s.startMatch("Argentina", "Australia");
        s.updateScore(m, 0, 5);
        s.updateScore(sp, 10, 2);
        s.updateScore(g, 2, 2);
        s.updateScore(u, 6, 6);
        s.updateScore(a, 3, 1);
        assertEquals(List.of("Uruguay", "Spain", "Mexico", "Argentina", "Germany"),
                s.getSummary().stream().map(MatchSummary::homeTeam).toList());
    }

    @Test
    void finishReturnsFinalResult() {
        Scoreboard s = new Scoreboard();
        UUID id = s.startMatch("Poland", "Germany");
        s.updateScore(id, 3, 2);
        MatchSummary r = s.finishMatch(id);
        assertEquals(3, r.homeScore());
        assertEquals(2, r.awayScore());
        assertTrue(s.getSummary().isEmpty());
    }

    @Test
    void cannotUpdateFinished() {
        Scoreboard s = new Scoreboard();
        UUID id = s.startMatch("A", "B");
        s.finishMatch(id);
        assertThrows(ScoreboardException.class, () -> s.updateScore(id, 1, 1));
    }
}