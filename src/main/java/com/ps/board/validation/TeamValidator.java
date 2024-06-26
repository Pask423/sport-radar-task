package com.ps.board.validation;

import com.ps.board.exceptions.EmptyTeamException;
import com.ps.board.exceptions.NotAllowedCharsInTeam;

import java.util.regex.Pattern;

public class TeamValidator {

    private static final Pattern VALID_TEAM_NAME_REGEX = Pattern.compile("^[A-Za-z ]+$");

    public void validateTeam(String team) {
        if (team == null || team.isBlank()) {
            throw new EmptyTeamException();
        }
        boolean matches = VALID_TEAM_NAME_REGEX.matcher(team).matches();
        if (!matches) {
            throw new NotAllowedCharsInTeam();
        }
    }
}
