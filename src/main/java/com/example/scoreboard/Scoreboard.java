package com.example.scoreboard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Scoreboard {
    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public UUID startMatch(String home, String away) {
        validate(home);
        validate(away);
        if (home.equalsIgnoreCase(away)) throw new ScoreboardException("Teams must differ");
        UUID id = UUID.randomUUID();
        Match m = new Match(id, home, away, sequence.incrementAndGet());
        lock.writeLock().lock();
        try {
            matches.put(id, m);
            return id;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateScore(UUID id, int home, int away) {
        if (home < 0 || away < 0) throw new ScoreboardException("Score cannot be negative");
        lock.writeLock().lock();
        try {
            Match m = getMatch(id);
            isActive(m);
            m.updateScore(home, away);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public MatchSummary finishMatch(UUID id) {
        lock.writeLock().lock();
        try {
            Match m = getMatch(id);
            isActive(m);
            m.finish();
            return getMatchSummary(m);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<MatchSummary> getSummary() {
        lock.readLock().lock();
        try {
            return matches.values().stream().filter(m -> m.getStatus() == MatchStatus.IN_PROGRESS)
                    .sorted(Comparator.comparingInt(Match::getTotalScore).reversed()
                            .thenComparing(Comparator.comparingLong(Match::getStartOrder).reversed()))
                    .map(this::getMatchSummary).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void cancelMatch(UUID id) {
        lock.writeLock().lock();
        try {
            Match m = getMatch(id);
            isActive(m);
            m.cancel();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Match getMatch(UUID id) {
        if (id == null) throw new ScoreboardException("ID is null");
        Match m = matches.get(id);
        if (m == null) throw new ScoreboardException("Match not found");
        return m;
    }

    private void isActive(Match m) {
        if (m.getStatus() != MatchStatus.IN_PROGRESS) throw new ScoreboardException("Match is not in progress");
    }

    private MatchSummary getMatchSummary(Match m) {
        return new MatchSummary(m.getId(), m.getHomeTeam(), m.getHomeScore(), m.getAwayTeam(), m.getAwayScore());
    }

    private void validate(String s) {
        if (s == null || s.isBlank()) throw new ScoreboardException("Team name is empty");
    }
}