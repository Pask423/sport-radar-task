package com.ps.board;

import com.ps.board.exceptions.EmptyTeamException;
import com.ps.board.exceptions.GameIdNullException;
import com.ps.board.exceptions.GameNotFoundException;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;
import com.ps.store.GameState;
import com.ps.store.GameStateMapper;
import com.ps.store.GamesStore;
import com.ps.time.TimeProvider;

import java.util.List;
import java.util.UUID;

import static com.ps.board.model.Game.DEFAULT_GAME_COMPARATOR;

public class DefaultScoreBoard implements ScoreBoard {

    private final GamesStore store;
    private final TimeProvider timeProvider;
    private final GameStateMapper gameStateMapper;

    public DefaultScoreBoard(GamesStore store, TimeProvider timeProvider) {
        this.store = store;
        this.gameStateMapper = new GameStateMapper();
        this.timeProvider = timeProvider;
    }

    @Override
    public NewGame startGame(String homeTeam, String awayTeam) {
        validateTeam(homeTeam, "Home team cannot be empty");
        validateTeam(awayTeam, "Away team cannot be empty");
        GameState initial = gameStateMapper.initial(UUID.randomUUID(), homeTeam, awayTeam, timeProvider.now());
        GameState gameState = store.create(initial);
        return gameStateMapper.toNewGame(gameState);
    }

    private void validateTeam(String team, String message) {
        if (team == null || team.isBlank()) {
            throw new EmptyTeamException(message);
        }
    }

    @Override
    public Game updateScore(UUID gameId, int homeTeamScore, int awayTeamScore) {
        validateGameId(gameId);
        return store.get(gameId)
                .map(gameState -> gameState.updateScore(homeTeamScore, awayTeamScore))
                .map(store::update)
                .map(gameStateMapper::toGame)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    private void validateGameId(UUID gameId) {
        if (gameId == null) {
            throw new GameIdNullException();
        }
    }

    @Override
    public FinishedGame finishGame(UUID gameId) {
        validateGameId(gameId);
        GameState state = store.get(gameId)
                .map(game -> store.delete(gameId))
                .orElseThrow(() -> new GameNotFoundException(gameId));
        return gameStateMapper.toFinishedGame(state);
    }

    @Override
    public ScoreBoardSummary getSummary() {
        List<Game> games = store.getAll()
                .stream()
                .map(gameStateMapper::toGame)
                .sorted(DEFAULT_GAME_COMPARATOR)
                .toList();
        return new ScoreBoardSummary(games);
    }
}