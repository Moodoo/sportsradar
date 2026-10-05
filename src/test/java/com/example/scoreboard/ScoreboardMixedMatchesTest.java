package com.example.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardMixedMatchesTest {

    @Test
    void shouldMixFinishedAndInProgressMatchesAndReturnOnlyActiveMatches() {
        Scoreboard scoreboard = new Scoreboard();

        UUID mexico = scoreboard.startMatch("Mexico", "Canada");
        UUID spain = scoreboard.startMatch("Spain", "Brazil");
        UUID germany = scoreboard.startMatch("Germany", "France");
        UUID uruguay = scoreboard.startMatch("Uruguay", "Italy");
        UUID argentina = scoreboard.startMatch("Argentina", "Australia");

        scoreboard.updateScore(mexico, 0, 5);
        scoreboard.updateScore(spain, 10, 2);
        scoreboard.updateScore(germany, 2, 2);
        scoreboard.updateScore(uruguay, 6, 6);
        scoreboard.updateScore(argentina, 3, 1);

        MatchSummary finishedMexico = scoreboard.finishMatch(mexico);
        MatchSummary finishedGermany = scoreboard.finishMatch(germany);

        assertEquals("Mexico", finishedMexico.homeTeam());
        assertEquals(0, finishedMexico.homeScore());
        assertEquals("Canada", finishedMexico.awayTeam());
        assertEquals(5, finishedMexico.awayScore());

        assertEquals("Germany", finishedGermany.homeTeam());
        assertEquals(2, finishedGermany.homeScore());
        assertEquals("France", finishedGermany.awayTeam());
        assertEquals(2, finishedGermany.awayScore());

        List<MatchSummary> summary = scoreboard.getSummary();

        assertEquals(3, summary.size());

        assertFalse(summary.stream()
                .anyMatch(m -> m.homeTeam().equals("Mexico")));

        assertFalse(summary.stream()
                .anyMatch(m -> m.homeTeam().equals("Germany")));

        assertEquals(
                List.of("Uruguay", "Spain", "Argentina"),
                summary.stream()
                        .map(MatchSummary::homeTeam)
                        .toList()
        );

        assertEquals(6, summary.get(0).homeScore());
        assertEquals(6, summary.get(0).awayScore());

        assertEquals(10, summary.get(1).homeScore());
        assertEquals(2, summary.get(1).awayScore());

        assertEquals(3, summary.get(2).homeScore());
        assertEquals(1, summary.get(2).awayScore());
    }
}
