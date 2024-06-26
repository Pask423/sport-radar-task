package com.ps.board;

import com.ps.board.exceptions.GameNotFoundException;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.NewGame;
import com.ps.store.InMemoryGamesStore;
import com.ps.time.DefaultTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultScoreBoardMultiThreadTest {

    private DefaultScoreBoard scoreBoard;

    @BeforeEach
    public void setUp() {
        InMemoryGamesStore store = new InMemoryGamesStore(new HashMap<>());
        scoreBoard = new DefaultScoreBoard(store, new DefaultTimeProvider());
    }

    @RepeatedTest(100)
    public void testConcurrentGameCreation() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        CompletableFuture<NewGame> startRequest1 = startRequest("PL", "ENG");
        CompletableFuture<NewGame> startRequest2 = startRequest("PL", "ENG");

        // When
        NewGame newGame1 = startRequest1.get(1, TimeUnit.SECONDS);
        NewGame newGame2 = startRequest2.get(1, TimeUnit.SECONDS);

        // Then
        assertThat(newGame1.gameId())
                .as("Id of two games should always be the same. If the concurrency is not working properly," +
                        " then we can expect that it will different as we are recreating game in concurrent manner.")
                .isEqualTo(newGame2.gameId());
    }

    private CompletableFuture<NewGame> startRequest(String homeTeam, String awayTeam) {
        return CompletableFuture.supplyAsync(() -> scoreBoard.startGame(homeTeam, awayTeam));
    }

    @RepeatedTest(1000)
    public void testConcurrentFinish() throws InterruptedException, TimeoutException {
        // Given
        NewGame newGame = scoreBoard.startGame("ENG", "GER");
        CompletableFuture<FinishedGame> finishRequest1 = finishRequest(newGame.gameId());
        CompletableFuture<FinishedGame> finishRequest2 = finishRequest(newGame.gameId());
        CompletableFuture<FinishedGame> finishRequest3 = finishRequest(newGame.gameId());
        ExecutionException executionException = null;
        int exceptionNumber = 0;

        // When
        try {
            finishRequest1.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            executionException = e;
            exceptionNumber++;
        }

        try {
            finishRequest2.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            executionException = e;
            exceptionNumber++;
        }

        try {
            finishRequest3.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            executionException = e;
            exceptionNumber++;
        }

        // Then
        assertThat(exceptionNumber).as("Two of the attempts should always fail").isEqualTo(2);
        assertThat(executionException).rootCause().isInstanceOf(GameNotFoundException.class);
    }

    private CompletableFuture<FinishedGame> finishRequest(UUID gameId) {
        return CompletableFuture.supplyAsync(() -> scoreBoard.finishGame(gameId));
    }
}
