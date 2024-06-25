package com.ps.store;

import java.util.*;

public class InMemoryGamesStore implements GamesStore {

    private final Map<UUID, GameState> store;

    public InMemoryGamesStore(Map<UUID, GameState> store) {
        this.store = store;
    }

    @Override
    public GameState create(GameState state) {
        store.putIfAbsent(state.gameId, state);
        return state;
    }

    @Override
    public Optional<GameState> get(UUID gameId) {
        return Optional.ofNullable(store.get(gameId));
    }

    @Override
    public GameState update(GameState state) {
        store.replace(state.gameId, state);
        return state;
    }

    @Override
    public GameState delete(UUID gameId) {
        return store.remove(gameId);
    }

    @Override
    public Set<GameState> getAll() {
        return new HashSet<>(store.values());
    }
}
