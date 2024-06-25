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
}