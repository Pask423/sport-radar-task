package com.ps.board;

import com.ps.board.exceptions.*;
import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;
import com.ps.board.model.ScoreBoardSummary;

import java.util.UUID;

/**
 * Interface for managing football games.
 */
public interface ScoreBoard {

    /**
     * Starts a new game with the specified home and away teams.
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @return a {@link NewGame} object representing the newly started game
     * @throws SameTeamsGameException while attempting to start game with same teams
     * @throws EmptyTeamException when one of the teams is null or blank
     * @throws NotAllowedCharsInTeam when one of the teams contains special characters or numbers
     */
    NewGame startGame(String homeTeam, String awayTeam);

    /**
     * Updates the score of an ongoing game.
     *
     * @param gameId        the unique identifier of the game
     * @param homeTeamScore the new score for the home team
     * @param awayTeamScore the new score for the away team
     * @return a {@link Game} object representing the updated game
     * @throws GameIdNullException for null gameId value
     * @throws GameNotFoundException for non-existing game
     * @throws NegativeScoreException for negative score values
     */
    Game updateScore(UUID gameId, int homeTeamScore, int awayTeamScore);

    /**
     * Finishes an ongoing game.
     *
     * @param gameId the unique identifier of the game
     * @return a {@link FinishedGame} object representing the finished game
     * @throws GameIdNullException for null gameId value
     * @throws GameNotFoundException for non-existing game
     */
    FinishedGame finishGame(UUID gameId);

    /**
     * Retrieves a summary of all ongoing games.
     * The games with the same total score will be returned ordered
     * by the most recently started match in the scoreboard
     *
     * @return a {@link ScoreBoardSummary} object containing the summary of games in progress
     */
    ScoreBoardSummary getSummary();
}