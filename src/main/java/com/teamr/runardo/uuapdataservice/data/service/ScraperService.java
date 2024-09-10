package com.teamr.runardo.uuapdataservice.data.service;

import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.scraper.gamescraper.ScraperManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ScraperService {
    private UaapDataService uaapDataService;

    @Autowired
    public ScraperService(UaapDataService uaapDataService) {
        this.uaapDataService = uaapDataService;
    }

    //    update Uaap Games---------------------------------------------------
    public void updateUaapSeasonGamesById(int id) {
        //get Uaap Season and convert to dto
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(id);
        UaapSeasonDto uaapSeasonDtoDb = UaapSeasonDto.convertToDto(uaapSeason);

        //create scraper
        ScraperManager scraperManager = new ScraperManager(uaapSeasonDtoDb);
        UaapSeasonDto uaapSeasonDtoToUpdate = scraperManager.getUaapSeasonDtoToSave();

        //game number in DB for not updated games
        HashSet<Integer> gameNumSet = new HashSet<>();
        uaapSeasonDtoDb.getUaapGames().stream().map(UaapGameDto::getGameNumber)
                .forEach(gameNumSet::add
                );

        for (UaapGameDto uaapGameDto : uaapSeasonDtoToUpdate.getUaapGames()) {
            UaapGame uaapGame = UaapGameDto.convertToEntity(uaapGameDto);

            if (gameNumSet.contains(uaapGame.getGameNumber())) {
                uaapDataService.deleteUaapGameByGameNumAndSeasonId(uaapGame.getGameNumber(),uaapGame.getSeasonId(), uaapSeasonDtoToUpdate.getGameCode().getGameCode());
            }

            int gameId = uaapDataService.saveUaapGame(uaapGame).getId();//saves UaapGame and GameResult
            for (GameResultDto gameResultDto : uaapGameDto.getGameResults()) {
                GameResult gameResult = GameResultDto.convertToEntity(gameResultDto);
                gameResult.setGameId(gameId);
                uaapDataService.saveGameResult(gameResult);

                for (PlayerStat playerStat : gameResultDto.getPlayerStats()) {
                    uaapDataService.savePlayerStat(playerStat);  //saves Stats and Players
                }
            }
        }
    }
}
