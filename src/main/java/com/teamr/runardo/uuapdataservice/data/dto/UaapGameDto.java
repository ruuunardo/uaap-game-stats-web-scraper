package com.teamr.runardo.uuapdataservice.data.dto;

import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
}