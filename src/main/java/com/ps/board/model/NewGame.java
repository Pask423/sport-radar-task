package com.ps.board.model;

import java.util.UUID;

public record NewGame(
        UUID gameId,
        String homeTeam,
        String awayTeam
) {
}