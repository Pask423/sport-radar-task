package com.ps.board.exceptions;

public class EmptyTeamException extends RuntimeException {
    public EmptyTeamException() {
        super("Team name cannot be empty");
    }
}
