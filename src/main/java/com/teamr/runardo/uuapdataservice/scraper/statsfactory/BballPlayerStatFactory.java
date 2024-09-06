package com.teamr.runardo.uuapdataservice.scraper.statsfactory;


import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.BasketballPlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.Player;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;

import java.util.Optional;

public class BballPlayerStatFactory implements PlayerStatsFactory{
    public BballPlayerStatFactory() {
    }

    @Override
    public Optional<PlayerStat> parse(UaapSeasonDto uaapSeasonDto, GameResultDto gameResultDto, String playerStatStr) {
        //split inline stat
        String[] splitStat = playerStatStr.split(",\\s*");

        //extract player details
        int univId = gameResultDto.getUniv().getId();
        String playerNumber = splitStat[0];
        String playerName = splitStat[1].replace("*", "");

        //create player
        Player extractedPlayer = Player.builder()
                .id(generateId(uaapSeasonDto, gameResultDto, playerNumber))
                .name(playerName)
                .univId(univId)
                .build();

        //create new player stat
        BasketballPlayerStat playerStat = BasketballPlayerStat.builder()
                .player(extractedPlayer)
//                .minPLayed(LocalTime.parse("0:".concat(splitStat[2]), DateTimeFormatter.ofPattern("H:m:s")))
//                .fieldGoalAttempts(Integer.parseInt(splitStat[3].split("/")[1]))
//                .fieldGoalMade(Integer.parseInt(splitStat[3].split("/")[0]))
//                .twoPointsAttempts(Integer.parseInt(splitStat[5].split("/")[1]))
//                .twoPointsMade(Integer.parseInt(splitStat[5].split("/")[0]))
//                .threePointsAttempts(Integer.parseInt(splitStat[7].split("/")[1]))
//                .threePointsMade(Integer.parseInt(splitStat[7].split("/")[0]))
//                .freeThrowAttempts(Integer.parseInt(splitStat[9].split("/")[1]))
//                .freeThrowMade(Integer.parseInt(splitStat[9].split("/")[0]))
//                .reboundsOR(Integer.parseInt(splitStat[11]))
//                .reboundsDR(Integer.parseInt(splitStat[12]))
//                .Assist(Integer.parseInt(splitStat[14]))
//                .turnOver(Integer.parseInt(splitStat[15]))
//                .steal(Integer.parseInt(splitStat[16]))
//                .block(Integer.parseInt(splitStat[17]))
//                .foulsPF(Integer.parseInt(splitStat[18]))
//                .foulsFD(Integer.parseInt(splitStat[19]))
//                .efficiency(Integer.parseInt(splitStat[20]))
                .points(Integer.parseInt(splitStat[21]))
//                .isFirstFive(splitStat[1].startsWith("*") ? 1 : 0)
//                .gameResult(gameResultDto)
                .build();

        if (playerStatStr.isBlank())
            return Optional.empty();

        return Optional.of(playerStat);
    }
}
