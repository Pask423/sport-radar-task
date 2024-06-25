package com.ps.store;

import com.ps.board.model.FinishedGame;
import com.ps.board.model.Game;
import com.ps.board.model.NewGame;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GameStateMapper {

    public Game toGame(GameState state) {
        return new Game(state.gameId, state.homeTeam, state.homeTeamScore, state.awayTeam, state.awayTeamScore, state.gameStart);
    }

    public NewGame toNewGame(GameState state) {
        return new NewGame(state.gameId, state.homeTeam, state.awayTeam);
    }

    public FinishedGame toFinishedGame(GameState state) {
        return new FinishedGame(state.gameId, state.homeTeam, state.homeTeamScore, state.awayTeam, state.awayTeamScore);
    }

    public GameState initial(UUID gameId, String homeTeam, String awayTeam, OffsetDateTime gameStart) {
        return GameState.initial(gameId, homeTeam, awayTeam, gameStart);
    }
}
