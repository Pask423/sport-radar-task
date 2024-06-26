package com.ps.store;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GameStateTest {

    @Test
    public void toIdentifierTest() {
        // Given
        String homeTeam = "PL";
        String awayTeam = "DE";
        GameState initial = GameState.initial(UUID.randomUUID(), homeTeam, awayTeam, OffsetDateTime.now());

        // When
        String identifier = initial.toIdentifier();

        // Then
        assertThat(identifier).isEqualTo("PLDE");
    }

    @Test
    public void scoreUnchangedTest() {
        // Given
        GameState initial = GameState.initial(UUID.randomUUID(), "PL", "DE", OffsetDateTime.now());

        // When
        boolean unchanged = initial.scoreUnchanged(0, 0);

        // Then
        assertThat(unchanged).isTrue();
    }

    @Test
    public void scoreUnchangedWithUpdateTest() {
        // Given
        GameState state = GameState.initial(UUID.randomUUID(), "PL", "DE", OffsetDateTime.now())
                .updateScore(3, 4);

        // When
        boolean unchanged = state.scoreUnchanged(4, 4);

        // Then
        assertThat(unchanged).isFalse();
    }
}