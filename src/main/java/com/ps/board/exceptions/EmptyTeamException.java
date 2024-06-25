package com.ps.board.exceptions;

public class EmptyTeamException extends RuntimeException {
    public EmptyTeamException(String message) {
        super(message);
    }
}
