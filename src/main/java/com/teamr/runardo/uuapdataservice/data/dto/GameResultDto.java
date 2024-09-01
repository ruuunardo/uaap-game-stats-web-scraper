package com.teamr.runardo.uuapdataservice.data.dto;

import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapUniv;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class GameResultDto {
    //<gameID-univCode>
    private String id;

    private int gameId;

    private UaapUniv univ;

    private String teamTag;

    private int finalScore;

    private List<PlayerStat> playerStats;

    public GameResultDto() {

    }

    public static GameResultDto convertToDto(GameResult gameResult) {
        GameResultDto gameResultDto = new GameResultDto();

        gameResultDto.setId(gameResult.getId());
        gameResultDto.setGameId(gameResult.getGameId());
        gameResultDto.setTeamTag(gameResult.getTeamTag());
        gameResultDto.setUniv(gameResult.getUniv());
        gameResultDto.setFinalScore(gameResult.getFinalScore());

        return gameResultDto;
    }
}
