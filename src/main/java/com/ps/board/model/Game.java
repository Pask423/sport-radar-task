package com.ps.board.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Game(
        UUID gameId,
        String homeTeam,
        int homeTeamScore,
        String awayTeam,
        int awayTeamScore,
        OffsetDateTime gameStart
) {
    public int totalScore() {
        return homeTeamScore + awayTeamScore;
    }
}