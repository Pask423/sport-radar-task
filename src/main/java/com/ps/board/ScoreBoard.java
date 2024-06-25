package com.ps.board;

import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;

import java.util.UUID;

public interface ScoreBoard {

    NewGame startGame(String homeTeam, String awayTeam);

    Game updateScore(UUID gameId, int homeTeamScore, int awayTeamScore);

    FinishedGame finishGame(UUID gameId);

    ScoreBoardSummary getSummary();
}