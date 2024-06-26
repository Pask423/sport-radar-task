package com.ps.board;

import com.ps.board.exceptions.EmptyTeamException;
import com.ps.board.exceptions.GameIdNullException;
import com.ps.board.exceptions.GameNotFoundException;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;
import com.ps.store.GamesStore;
import com.ps.store.InMemoryGamesStore;
import com.ps.time.DefaultTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultScoreBoardTest {

    private ScoreBoard scoreBoard;

    @BeforeEach
    public void setUp() {
        GamesStore store = new InMemoryGamesStore(new HashMap<>());
        scoreBoard = new DefaultScoreBoard(store, new DefaultTimeProvider());
    }

    @Test
    public void startGameTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";

        // When
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);

        // Then
        assertThat(newGame.gameId()).isNotNull();
        assertThat(newGame.homeTeam()).isEqualTo(homeTeam);
        assertThat(newGame.awayTeam()).isEqualTo(awayTeam);
    }

    @Test
    public void startWithNullTest() {
        // Then
        assertThatThrownBy(() -> scoreBoard.startGame(null, "Test"))
                .isInstanceOf(EmptyTeamException.class);
    }

    @Test
    public void startWithEmptyTest() {
        // Then
        assertThatThrownBy(() -> scoreBoard.startGame("Test", " "))
                .isInstanceOf(EmptyTeamException.class);
    }

    @Test
    public void updateScoreTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";
        int homeTeamScore = 4;
        int awayTeamScore = 2;
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);

        // When
        Game updatedGame = scoreBoard.updateScore(newGame.gameId(), homeTeamScore, awayTeamScore);

        // Then
        assertThat(updatedGame.awayTeam()).isEqualTo(awayTeam);
        assertThat(updatedGame.awayTeamScore()).isEqualTo(awayTeamScore);
        assertThat(updatedGame.homeTeam()).isEqualTo(homeTeam);
        assertThat(updatedGame.homeTeamScore()).isEqualTo(homeTeamScore);
    }

    @Test
    public void updateNullTest() {
        // Then
        assertThatThrownBy(() -> scoreBoard.updateScore(null, 0, 0))
                .isInstanceOf(GameIdNullException.class);
    }

    @Test
    public void updateNotExistingTest() {
        // When
        UUID nonExistingId = UUID.randomUUID();

        // Then
        assertThatThrownBy(() -> scoreBoard.updateScore(nonExistingId, 0, 0))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    public void finishGameTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";
        int homeTeamScore = 4;
        int awayTeamScore = 2;
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);
        UUID gameId = newGame.gameId();
        scoreBoard.updateScore(gameId, homeTeamScore, awayTeamScore);

        // When
        FinishedGame finishedGame = scoreBoard.finishGame(gameId);

        // Then
        assertThat(finishedGame).isNotNull();
        assertThatThrownBy(() -> scoreBoard.updateScore(gameId, 0, 0))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    public void nullFinishTest() {
        // Then
        assertThatThrownBy(() -> scoreBoard.finishGame(null))
                .isInstanceOf(GameIdNullException.class);
    }

    @Test
    public void finishGameShouldNotModifyScoreTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);
        UUID gameId = newGame.gameId();

        // When
        FinishedGame finishedGame = scoreBoard.finishGame(gameId);

        // Then
        assertThat(finishedGame).isNotNull();
        assertThatThrownBy(() -> scoreBoard.updateScore(gameId, 0, 0))
                .isInstanceOf(GameNotFoundException.class);
        assertThat(finishedGame.awayTeam()).isEqualTo(awayTeam);
        assertThat(finishedGame.awayTeamScore()).isZero();
        assertThat(finishedGame.homeTeam()).isEqualTo(homeTeam);
        assertThat(finishedGame.homeTeamScore()).isZero();
    }

    @Test
    public void finishedNotExistingTest() {
        // When
        UUID nonExistingId = UUID.randomUUID();

        // Then
        assertThatThrownBy(() -> scoreBoard.finishGame(nonExistingId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    public void getSummaryTest() {
        // Given
        setupGame("Mexico", 0,"Canada", 5);
        setupGame("Spain", 10, "Brazil", 2);
        setupGame("Germany", 2, "France", 2);
        setupGame("Uruguay", 6, "Italy", 6);
        setupGame("Argentina", 3, "Australia", 1);

        // When
        ScoreBoardSummary summary = scoreBoard.getSummary();

        // Then
        List<Game> games = summary.games();
        assertThat(games).hasSize(5);
        compareGame(games.get(0), "Uruguay", 6, "Italy", 6);
        compareGame(games.get(1), "Spain", 10, "Brazil", 2);
        compareGame(games.get(2), "Mexico", 0, "Canada", 5);
        compareGame(games.get(3), "Argentina", 3, "Australia", 1);
        compareGame(games.get(4), "Germany", 2, "France", 2);
    }

    private void setupGame(String homeTeam, int homeTeamScore, String awayTeam, int awayTeamScore) {
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);
        scoreBoard.updateScore(newGame.gameId(), homeTeamScore, awayTeamScore);
    }

    private void compareGame(Game game, String homeTeam, int homeTeamScore, String awayTeam, int awayTeamScore) {
        assertThat(game.homeTeam()).isEqualTo(homeTeam);
        assertThat(game.homeTeamScore()).isEqualTo(homeTeamScore);
        assertThat(game.awayTeam()).isEqualTo(awayTeam);
        assertThat(game.awayTeamScore()).isEqualTo(awayTeamScore);
    }
}
