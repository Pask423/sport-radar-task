package com.ps.board.exceptions;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Game with id %s was does not exist".formatted(gameId.toString()));
    }
}
