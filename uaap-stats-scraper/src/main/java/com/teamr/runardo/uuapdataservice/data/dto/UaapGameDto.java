package com.teamr.runardo.uuapdataservice.data.dto;

import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class UaapGameDto {
    private int id;

    private int gameNumber;

    private LocalDateTime gameSched;

    private String venue;

    private int seasonId;

    private List<GameResultDto> gameResults;

    public UaapGameDto() {
    }

    public static UaapGameDto convertToDto(UaapGame uaapGame) {
        UaapGameDto uaapGameDto = new UaapGameDto();

        uaapGameDto.setId(uaapGame.getId());
        uaapGameDto.setGameNumber(uaapGame.getGameNumber());
        uaapGameDto.setGameSched(uaapGame.getGameSched());
        uaapGameDto.setVenue(uaapGame.getVenue());
        uaapGameDto.setSeasonId(uaapGame.getSeasonId());
        if (uaapGame.getGameResults() != null) {
            uaapGameDto.setGameResults(uaapGame.getGameResults().stream()
                                .map(GameResultDto::convertToDto)
                                .toList()
            );
        }

        return uaapGameDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UaapGameDto that = (UaapGameDto) o;

        int finalScoreThis = gameResults.stream().map(GameResultDto::getFinalScore).mapToInt(Integer::intValue).sum();
        int finalScoreThat = that.gameResults.stream().map(GameResultDto::getFinalScore).mapToInt(Integer::intValue).sum();
        return gameNumber == that.gameNumber && seasonId == that.seasonId & finalScoreThat == finalScoreThis;
//        return gameNumber == that.gameNumber && seasonId == that.seasonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameNumber, seasonId);
    }

    @Override
    public String toString() {
        return "UaapGameDto{" +
                "id=" + id +
                ", gameNumber=" + gameNumber +
                ", gameSched=" + gameSched +
                ", venue='" + venue + '\'' +
                ", seasonId=" + seasonId +
                ", gameResults=" + gameResults +
                '}';
    }

    public static UaapGame convertToEntity(UaapGameDto uaapGameDto) {
        UaapGame build = UaapGame.builder()
                .gameNumber(uaapGameDto.getGameNumber())
                .id(uaapGameDto.getId())
                .gameSched(uaapGameDto.getGameSched())
                .venue(uaapGameDto.getVenue())
                .seasonId(uaapGameDto.getSeasonId())
                .build();

        if (uaapGameDto.gameResults != null) {
            List<GameResult> list = uaapGameDto.gameResults.stream()
                    .map(GameResultDto::convertToEntity)
                    .toList();
            build.setGameResults(list);
        }
        return build;
    }

}