package com.ps.board.model;

import java.util.UUID;

public record FinishedGame(
        UUID gameId,
        String homeTeam,
        int homeTeamScore,
        String awayTeam,
        int awayTeamScore
) {
}