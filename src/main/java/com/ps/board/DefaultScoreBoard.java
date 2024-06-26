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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.ps.board.GameComparator.DEFAULT;

public class DefaultScoreBoard implements ScoreBoard {

    private final GamesStore store;
    private final TimeProvider timeProvider;
    private final GameStateMapper gameStateMapper;
    private final Map<UUID, ReentrantLock> gamesLock;
    private final Map<String, UUID> gamesIdentifiersToId;

    public DefaultScoreBoard(GamesStore store, TimeProvider timeProvider) {
        this.store = store;
        this.timeProvider = timeProvider;
        this.gameStateMapper = new GameStateMapper();
        this.gamesLock = new ConcurrentHashMap<>();
        this.gamesIdentifiersToId = new ConcurrentHashMap<>();
    }

    @Override
    public NewGame startGame(String homeTeam, String awayTeam) {
        validateTeam(homeTeam, "Home team cannot be empty");
        validateTeam(awayTeam, "Away team cannot be empty");
        UUID gameId = gamesIdentifiersToId.computeIfAbsent(homeTeam.concat(awayTeam), id -> UUID.randomUUID());
        ReentrantLock lock = gamesLock.computeIfAbsent(gameId, id -> new ReentrantLock());
        try {
            lock.lock();
            GameState initial = gameStateMapper.initial(gameId, homeTeam, awayTeam, timeProvider.now());
            GameState gameState = store.create(initial);
            return gameStateMapper.toNewGame(gameState);
        } finally {
            lock.unlock();
        }
    }

    private void validateTeam(String team, String message) {
        if (team == null || team.isBlank()) {
            throw new EmptyTeamException(message);
        }
    }

    @Override
    public Game updateScore(UUID gameId, int homeTeamScore, int awayTeamScore) {
        validateGameId(gameId);
        ReentrantLock lock = gamesLock.computeIfAbsent(gameId, id -> new ReentrantLock());
        try {
            lock.lock();
            GameState gameState = store.get(gameId)
                    .orElseThrow(() -> new GameNotFoundException(gameId));
            if (gameState.scoreUnchanged(homeTeamScore, awayTeamScore)) {
                return gameStateMapper.toGame(gameState);
            } else {
                GameState stateWithNewScore = gameState.updateScore(homeTeamScore, awayTeamScore);
                GameState updateState = store.update(stateWithNewScore);
                return gameStateMapper.toGame(updateState);
            }
        } finally {
            lock.unlock();
        }
    }

    private void validateGameId(UUID gameId) {
        if (gameId == null) {
            throw new GameIdNullException();
        }
    }

    @Override
    public FinishedGame finishGame(UUID gameId) {
        validateGameId(gameId);
        ReentrantLock lock = gamesLock.computeIfAbsent(gameId, id -> new ReentrantLock());
        try {
            lock.lock();
            GameState state = store.get(gameId)
                    .map(e -> store.delete(gameId))
                    .orElseThrow(() -> new GameNotFoundException(gameId));

            gamesIdentifiersToId.remove(state.toIdentifier());
            return gameStateMapper.toFinishedGame(state);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ScoreBoardSummary getSummary() {
        List<Game> games = store.getAll()
                .stream()
                .map(gameStateMapper::toGame)
                .sorted(DEFAULT)
                .toList();
        return new ScoreBoardSummary(games);
    }
}