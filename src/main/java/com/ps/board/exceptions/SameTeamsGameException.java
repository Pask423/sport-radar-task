package com.ps.board.exceptions;

public class SameTeamsGameException extends RuntimeException {
    public SameTeamsGameException() {
        super("Team names must be different");
    }
}
