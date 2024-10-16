package com.teamr.runardo.uuapdataservice.scraper.statsfactory;


import com.teamr.runardo.uuapdataservice.data.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.data.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.BasketballPlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.Player;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        String playerNumber = splitStat[0].replace("*", "");
        String playerName = splitStat[1];

        //create player
        Player extractedPlayer = Player.builder()
                .id(generateId(uaapSeasonDto, gameResultDto, playerNumber))
                .name(playerName)
                .univId(univId)
                .build();

//        "*28,K. Quiambao,,32:02,22,9-22,40.9,6-14,42.9,3-8,37.5,1-1,100.0,3,5,8,7,5,0,0,0,4,6"
        //create new player stat
        BasketballPlayerStat playerStat = BasketballPlayerStat.builder()
                .player(extractedPlayer)
                .minPlayed(LocalTime.parse("0:".concat(splitStat[3]), DateTimeFormatter.ofPattern("H:m:s")))
                .points(Integer.parseInt(splitStat[4]))
                .fieldGoalAttempts(Integer.parseInt(splitStat[5].split("-")[1]))
                .fieldGoalMade(Integer.parseInt(splitStat[5].split("-")[0]))
                .twoPointsAttempts(Integer.parseInt(splitStat[7].split("-")[1]))
                .twoPointsMade(Integer.parseInt(splitStat[7].split("-")[0]))
                .threePointsAttempts(Integer.parseInt(splitStat[9].split("-")[1]))
                .threePointsMade(Integer.parseInt(splitStat[9].split("-")[0]))
                .freeThrowAttempts(Integer.parseInt(splitStat[11].split("-")[1]))
                .freeThrowMade(Integer.parseInt(splitStat[11].split("-")[0]))
                .reboundsOR(Integer.parseInt(splitStat[13]))
                .reboundsDR(Integer.parseInt(splitStat[14]))
                .assist(Integer.parseInt(splitStat[16]))
                .turnOver(Integer.parseInt(splitStat[17]))
                .steal(Integer.parseInt(splitStat[18]))
                .block(Integer.parseInt(splitStat[19]))
                .foulsPF(Integer.parseInt(splitStat[20]))
                .foulsFD(Integer.parseInt(splitStat[21]))
                .efficiency(Integer.parseInt(splitStat[22]))
                .isFirstFive(splitStat[0].startsWith("*") ? 1 : 0)
                .gameResult(gameResultDto.getId())
                .build();

        if (playerStatStr.isBlank())
            return Optional.empty();

        return Optional.of(playerStat);
    }
}
