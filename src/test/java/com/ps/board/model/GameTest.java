package com.ps.board.model;

import com.ps.store.GameState;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GameTest {

    @Test
    public void totalScoreTest() {
        // Given
        int homeTeamScore = 4;
        int awayTeamScore = 1;
        Game game = new Game(UUID.randomUUID(), "PL", homeTeamScore, "DE", awayTeamScore, OffsetDateTime.now());

        // When
        int totalScore = game.totalScore();

        // Then
        assertThat(totalScore).isEqualTo(5);
    }
}
