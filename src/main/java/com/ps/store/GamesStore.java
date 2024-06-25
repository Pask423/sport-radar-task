package com.ps.store;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for managing game states in a games store.
 */
public interface GamesStore {

    /**
     * Creates a new game state with the given details.
     *
     * @param state {@link GameState} the initial state of Game to save
     * @return the created {@link GameState} object
     */
    GameState create(GameState state);

    /**
     * Retrieves the game state for the specified game ID.
     *
     * @param gameId the unique identifier of the game
     * @return an {@link Optional} containing the {@link GameState} if found, or empty if not found
     */
    Optional<GameState> get(UUID gameId);

    /**
     * Updates the given game state.
     *
     * @param state the {@link GameState} object to update
     * @return the updated {@link GameState} object
     */
    GameState update(GameState state);

    /**
     * Deletes the game state for the specified game ID.
     *
     * @param gameId the unique identifier of the game
     * @return the deleted {@link GameState} object
     */
    GameState delete(UUID gameId);

    /**
     * Retrieves all games.
     *
     * @return a {@link Set} containing all {@link GameState} objects
     */
    Set<GameState> getAll();
}