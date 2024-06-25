package com.ps.board.model;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.UUID;

public record Game(
        UUID gameId,
        String homeTeam,
        int homeTeamScore,
        String awayTeam,
        int awayTeamScore,
        OffsetDateTime gameStart
) {
    public static final Comparator<Game> DEFAULT_GAME_COMPARATOR = Comparator
            .comparingInt(Game::totalScore)
            .thenComparing(Game::gameStart)
            .reversed();

    private int totalScore() {
        return homeTeamScore + awayTeamScore;
    }
}