package com.ps.store;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GameState {

    final UUID gameId;
    final String homeTeam;
    final int homeTeamScore;
    final String awayTeam;
    final int awayTeamScore;
    final OffsetDateTime gameStart;

    private GameState(UUID gameId, String homeTeam, int homeTeamScore, String awayTeam, int awayTeamScore, OffsetDateTime gameStart) {
        this.gameId = gameId;
        this.homeTeam = homeTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeam = awayTeam;
        this.awayTeamScore = awayTeamScore;
        this.gameStart = gameStart;
    }

    static GameState initial(UUID id, String homeTeam, String awayTeam, OffsetDateTime gameStart) {
        return new GameState(id, homeTeam, 0, awayTeam, 0, gameStart);
    }

    public GameState updateScore(int homeTeamScore, int awayTeamScore) {
        return new GameState(this.gameId, this.homeTeam, homeTeamScore, this.awayTeam, awayTeamScore, this.gameStart);
    }

    public String toIdentifier() {
        return this.homeTeam.concat(this.awayTeam);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "gameId=" + gameId +
                ", homeTeam='" + homeTeam + '\'' +
                ", homeTeamScore=" + homeTeamScore +
                ", awayTeam='" + awayTeam + '\'' +
                ", awayTeamScore=" + awayTeamScore +
                ", gameStart=" + gameStart +
                '}';
    }
}