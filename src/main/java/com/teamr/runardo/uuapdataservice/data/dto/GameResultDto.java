package com.teamr.runardo.uuapdataservice.data.dto;

import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapUniv;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
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

    public GameResultDto(GameResultDtoBuilder gameResultDtoBuilder) {
        this.id = gameResultDtoBuilder.id;
        this.gameId = gameResultDtoBuilder.gameId;
        this.univ = gameResultDtoBuilder.univ;
        this.teamTag = gameResultDtoBuilder.teamTag;
        this.finalScore = gameResultDtoBuilder.finalScore;
        this.playerStats = gameResultDtoBuilder.playerStats;
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

    public static GameResult convertToEntity(GameResultDto gameResultDto) {
        return GameResult.builder()
                .id(gameResultDto.id)
                .gameId(gameResultDto.gameId)
                .finalScore(gameResultDto.finalScore)
                .teamTag(gameResultDto.teamTag)
                .univ(gameResultDto.univ)
                .build();
    }


    public static class GameResultDtoBuilder {
        //<gameID-univCode>
        private String id;
        private int gameId;
        private UaapUniv univ;
        private String teamTag;
        private int finalScore;
        private List<PlayerStat> playerStats;

        public GameResultDtoBuilder() {
        }

        public GameResultDtoBuilder setId(UaapGameDto gameDto, UaapSeasonDto seasonDto) {
            this.id = String.join("-",
                    String.valueOf(seasonDto.getSeasonNumber()),
                    seasonDto.getGameCode().getGameCode(),
                    String.valueOf(gameDto.getGameNumber()),
                    univ.getUnivCode());
            return this;
        }

        public GameResultDtoBuilder setGameId(int gameDto) {
            this.gameId = gameDto;
            return this;
        }

        public GameResultDtoBuilder setUniv(UaapUniv univ) {
            this.univ = univ;
            return this;
        }

        public GameResultDtoBuilder setTeamTag(String teamTag) {
            this.teamTag = teamTag;
            return this;
        }

        public GameResultDtoBuilder setFinalScore(int finalScore) {
            this.finalScore = finalScore;
            return this;
        }

        public GameResultDtoBuilder setPlayerStats(List<PlayerStat> playerStats) {
            this.playerStats = playerStats;
            return this;
        }

        // Id created in build
        public GameResultDto build() {
//            assert gameId != 0;
            assert univ != null;

            return new GameResultDto(this);
        }
    }
}
