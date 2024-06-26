package com.ps.board.exceptions;

public class NegativeScoreException extends RuntimeException {
    public NegativeScoreException(String message) {
        super(message);
    }
}
