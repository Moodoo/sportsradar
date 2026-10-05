package com.example.scoreboard;

import java.util.UUID;

public class Match {
    private final UUID id;
    private final String homeTeam, awayTeam;
    private final long startOrder;
    private int homeScore, awayScore;
    private MatchStatus status = MatchStatus.IN_PROGRESS;

    public Match(UUID id, String homeTeam, String awayTeam, long startOrder) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startOrder = startOrder;
    }

    public UUID getId() {
        return id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public long getStartOrder() {
        return startOrder;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public int getTotalScore() {
        return homeScore + awayScore;
    }

    public void updateScore(int h, int a) {
        homeScore = h;
        awayScore = a;
    }

    public void finish() {
        status = MatchStatus.FINISHED;
    }

    public void cancel() {
        status = MatchStatus.CANCELLED;
    }
}