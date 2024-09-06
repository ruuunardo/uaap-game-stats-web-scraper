package com.teamr.runardo.uuapdataservice.scraper.statsfactory;

import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapTeam;

import java.util.Optional;

public interface PlayerStatsFactory {
    Optional<PlayerStat> parse(UaapSeasonDto uaapSeasonDto, GameResultDto gameResultDto, String playerStatStr);

    default String generateId(UaapSeasonDto uaapSeasonDto,GameResultDto gameResultDto, String playerNumber) {
        int univId = gameResultDto.getUniv().getId();
        UaapTeam uaapTeam = UaapTeam.parse(univId);
        return String.join("-", String.valueOf(uaapSeasonDto.getSeasonNumber()), uaapSeasonDto.getGameCode().getGameCode(), uaapTeam.toString(), playerNumber);
    }
}
