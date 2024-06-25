package com.ps.board;

import com.ps.board.exceptions.GameNotFoundException;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;
import com.ps.store.GamesStore;
import com.ps.time.DefaultTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultScoreBoardTest {

    private ScoreBoard scoreBoard;
    private GamesStore store;

    @BeforeEach
    public void setUp() {
        scoreBoard = new DefaultScoreBoard(store, new DefaultTimeProvider());
    }

    @Test
    public void testStartGame() {
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
    public void testUpdateScore() {
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
    public void testUpdateNotExisting() {
        // When
        UUID nonExistingId = UUID.randomUUID();

        // Then
        assertThatThrownBy(() -> scoreBoard.updateScore(nonExistingId, 0, 0))
                .isInstanceOf(GameNotFoundException.class);
    }


    @Test
    public void testFinishGame() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";
        int homeTeamScore = 4;
        int awayTeamScore = 2;
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);
        scoreBoard.updateScore(newGame.gameId(), homeTeamScore, awayTeamScore);

        // When
        FinishedGame finishedGame = scoreBoard.finishGame(newGame.gameId());

        // Then
        assertThat(finishedGame).isNotNull();
    }

    @Test
    public void finishGameShouldNotModifyScoreTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "BEL";
        NewGame newGame = scoreBoard.startGame(homeTeam, awayTeam);

        // When
        FinishedGame finishedGame = scoreBoard.finishGame(newGame.gameId());

        // Then
        assertThat(finishedGame).isNotNull();
        assertThat(finishedGame.awayTeam()).isEqualTo(awayTeam);
        assertThat(finishedGame.awayTeamScore()).isZero();
        assertThat(finishedGame.homeTeam()).isEqualTo(homeTeam);
        assertThat(finishedGame.homeTeamScore()).isZero();
    }

    @Test
    public void testFinishedNotExisting() {
        // When
        UUID nonExistingId = UUID.randomUUID();

        // Then
        assertThatThrownBy(() -> scoreBoard.finishGame(nonExistingId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    public void testGetSummary() {
        // Given
        NewGame newGame = scoreBoard.startGame("Mexico", "Canada");
        scoreBoard.updateScore(newGame.gameId(), 0, 5);
        NewGame newGame1 = scoreBoard.startGame("Spain", "Brazil");
        scoreBoard.updateScore(newGame1.gameId(), 10, 2);
        NewGame newGame2 = scoreBoard.startGame("Germany", "France");
        scoreBoard.updateScore(newGame2.gameId(), 2, 2);
        NewGame newGame3 = scoreBoard.startGame("Uruguay", "Italy");
        scoreBoard.updateScore(newGame3.gameId(), 6, 6);
        NewGame newGame4 = scoreBoard.startGame("Argentina", "Australia");
        scoreBoard.updateScore(newGame4.gameId(), 3, 1);

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

    private void compareGame(Game game, String homeTeam, int homeTeamScore, String awayTeam, int awayTeamScore) {
        assertThat(game.homeTeam()).isEqualTo(homeTeam);
        assertThat(game.homeTeamScore()).isEqualTo(homeTeamScore);
        assertThat(game.awayTeam()).isEqualTo(awayTeam);
        assertThat(game.awayTeamScore()).isEqualTo(awayTeamScore);
    }
}
