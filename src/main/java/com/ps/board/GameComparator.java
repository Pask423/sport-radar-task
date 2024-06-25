package com.ps.board;

import com.ps.board.model.Game;

import java.util.Comparator;

class GameComparator {

    private GameComparator() {
    }

    static final Comparator<Game> DEFAULT = Comparator
            .comparingInt(Game::totalScore)
            .thenComparing(Game::gameStart)
            .reversed();
}
