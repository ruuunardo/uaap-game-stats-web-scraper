package com.teamr.runardo.uuapdataservice.scraper.statsfactory;

import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.Player;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.VolleyballPlayerStat;

import java.util.Optional;

public class VballPlayerStatFactory implements PlayerStatsFactory {
    @Override
    public Optional<PlayerStat> parse(UaapSeasonDto uaapSeasonDto, GameResultDto gameResultDto, String playerStatStr) {
        //split inline stat
        String[] splitStat = playerStatStr.split(",\\s*");

        //extract player details
        int univId = gameResultDto.getUniv().getId();
        String playerNumber = splitStat[0].split("(?<=\\d)(?=\\D)")[0].replace("#", "");
        String playerName = splitStat[0].split("(?<=\\d)(?=\\D)")[1].trim();

        //create player
        Player extractedPlayer = Player.builder()
                .id(generateId(uaapSeasonDto, gameResultDto, playerNumber))
                .name(playerName)
                .univId(univId)
                .build();

        //create new player stat
        VolleyballPlayerStat playerStat = VolleyballPlayerStat.builder()
                .player(extractedPlayer)
                .attackMade(Integer.parseInt(splitStat[1]))
                .attackAttempt(Integer.parseInt(splitStat[2]))
//                .blockMade(Integer.parseInt(splitStat[3]))
//                .blockAttempt(Integer.parseInt(splitStat[4]))
//                .serveMade(Integer.parseInt(splitStat[5]))
//                .serveAttempt(Integer.parseInt(splitStat[6]))
//                .digMade(Integer.parseInt(splitStat[7]))
//                .digAttempt(Integer.parseInt(splitStat[8]))
//                .receiveMade(Integer.parseInt(splitStat[9]))
//                .receiveAttempt(Integer.parseInt(splitStat[10]))
//                .setMade(Integer.parseInt(splitStat[11]))
//                .setAttempt(Integer.parseInt(splitStat[12]))
                .build();

        if (playerStatStr.isBlank())
            return Optional.empty();

        return Optional.of(playerStat);
    }
}
