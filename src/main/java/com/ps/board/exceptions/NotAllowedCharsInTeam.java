package com.ps.board.exceptions;

public class NotAllowedCharsInTeam extends RuntimeException {
    public NotAllowedCharsInTeam() {
        super("Team name can only contain small letter, big letter and spaces");
    }
}
