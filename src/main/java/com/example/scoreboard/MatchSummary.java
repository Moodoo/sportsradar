package com.example.scoreboard;

import java.util.UUID;

public record MatchSummary(UUID id, String homeTeam, int homeScore, String awayTeam, int awayScore) {
    public int totalScore() {
        return homeScore + awayScore;
    }
}