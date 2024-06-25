package com.ps.store;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GamesStore {

    GameState create(GameState state);

    Optional<GameState> get(UUID gameId);

    GameState update(GameState state);

    GameState delete(UUID gameId);

    Set<GameState> getAll();
}