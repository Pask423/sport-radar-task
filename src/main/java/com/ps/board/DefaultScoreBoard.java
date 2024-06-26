package com.ps.board;

import com.ps.board.exceptions.GameIdNullException;
import com.ps.board.exceptions.GameNotFoundException;
import com.ps.board.exceptions.NegativeScoreException;
import com.ps.board.exceptions.SameTeamsGameException;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;
import com.ps.board.validation.TeamValidator;
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
    private final TeamValidator teamValidator;
    private final Map<UUID, ReentrantLock> gamesLock;
    private final Map<String, UUID> gamesIdentifiersToId;

    public DefaultScoreBoard(GamesStore store, TimeProvider timeProvider) {
        this.store = store;
        this.timeProvider = timeProvider;
        this.gameStateMapper = new GameStateMapper();
        this.teamValidator = new TeamValidator();
        this.gamesLock = new ConcurrentHashMap<>();
        this.gamesIdentifiersToId = new ConcurrentHashMap<>();
    }

    @Override
    public NewGame startGame(String homeTeam, String awayTeam) {
        teamValidator.validateTeam(homeTeam);
        teamValidator.validateTeam(awayTeam);
        String homeTeamName = normalizeTeamName(homeTeam);
        String awayTeamName = normalizeTeamName(awayTeam);
        if (homeTeamName.equals(awayTeamName)) {
            throw new SameTeamsGameException();
        }
        UUID gameId = gamesIdentifiersToId.computeIfAbsent(homeTeamName.concat(awayTeamName), id -> UUID.randomUUID());
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

    private static String normalizeTeamName(String homeTeam) {
        return homeTeam.toLowerCase().trim();
    }

    @Override
    public Game updateScore(UUID gameId, int homeTeamScore, int awayTeamScore) {
        validateGameId(gameId);
        checkNonNegative(homeTeamScore, "Home score cannot be negative");
        checkNonNegative(awayTeamScore, "Away score cannot be negative");
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

    private void checkNonNegative(int score, String message) {
        if (score < 0) {
            throw new NegativeScoreException(message);
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