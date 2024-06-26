package com.ps.board.validation;

import com.ps.board.exceptions.NotAllowedCharsInTeam;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

class TeamValidatorTest {

    TeamValidator validator = new TeamValidator();

    @ParameterizedTest
    @MethodSource("correctNamesProvider")
    public void validateTeamNoExceptionalTest(String input) {
        // Then
        assertThatNoException()
                .isThrownBy(() -> validator.validateTeam(input));
    }

    private static List<Arguments> correctNamesProvider() {
        return List.of(
                Arguments.of("POL"),
                Arguments.of("Pol"),
                Arguments.of("Real Madrid"),
                Arguments.of("Poland"),
                Arguments.of("RealMadrid")
        );
    }

    @ParameterizedTest
    @MethodSource("incorrectNamesProvider")
    public void validateTeamTest(String input) {
        // Then
        assertThatThrownBy(() -> validator.validateTeam(input))
                .isInstanceOf(NotAllowedCharsInTeam.class);
    }

    private static List<Arguments> incorrectNamesProvider() {
        return List.of(
                Arguments.of("test123"),
                Arguments.of("testł"),
                Arguments.of("  -l"),
                Arguments.of("123"),
                Arguments.of("ą"),
                Arguments.of("test!")
        );
    }

}