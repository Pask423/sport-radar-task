package com.ps.store;

import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameMapperTest {

    GameStateMapper mapper = new GameStateMapper();

    @Test
    public void toNewGameTest() {
        // Given
        UUID id = UUID.randomUUID();
        String homeTeam = "PL";
        String awayTeam = "DE";
        GameState initial = GameState.initial(id, homeTeam, awayTeam, OffsetDateTime.now());

        // When
        NewGame game = mapper.toNewGame(initial);

        // Then
        assertThat(game.awayTeam()).isEqualTo(awayTeam);
        assertThat(game.homeTeam()).isEqualTo(homeTeam);
        assertThat(game.gameId()).isEqualTo(id);
    }

    @Test
    public void toGameTest() {
        // Given
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        String homeTeam = "ENG";
        String awayTeam = "LR";
        int homeTeamScore = 2;
        int awayTeamScore = 4;
        GameState state = GameState
                .initial(id, homeTeam, awayTeam, now)
                .updateScore(homeTeamScore, awayTeamScore);

        // When
        Game game = mapper.toGame(state);

        // Then
        assertThat(game.awayTeam()).isEqualTo(awayTeam);
        assertThat(game.awayTeamScore()).isEqualTo(awayTeamScore);
        assertThat(game.homeTeam()).isEqualTo(homeTeam);
        assertThat(game.homeTeamScore()).isEqualTo(homeTeamScore);
        assertThat(game.gameId()).isEqualTo(id);
        assertThat(game.gameStart()).isEqualTo(now);
    }

    @Test
    public void toFinishedGameTest() {
        // Given
        UUID id = UUID.randomUUID();
        String homeTeam = "HUN";
        String awayTeam = "DE";
        int homeTeamScore = 1;
        int awayTeamScore = 3;
        GameState state = GameState
                .initial(id, homeTeam, awayTeam, OffsetDateTime.now())
                .updateScore(homeTeamScore, awayTeamScore);

        // When
        FinishedGame game = mapper.toFinishedGame(state);

        // Then
        assertThat(game.awayTeam()).isEqualTo(awayTeam);
        assertThat(game.awayTeamScore()).isEqualTo(awayTeamScore);
        assertThat(game.homeTeam()).isEqualTo(homeTeam);
        assertThat(game.homeTeamScore()).isEqualTo(homeTeamScore);
        assertThat(game.gameId()).isEqualTo(id);
    }
}