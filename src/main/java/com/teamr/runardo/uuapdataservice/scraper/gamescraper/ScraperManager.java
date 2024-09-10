package com.teamr.runardo.uuapdataservice.scraper.gamescraper;


import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.utility.UtilityClass;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScraperManager {
    private final GameScraper gameScraper;
    private final UaapSeasonDto uaapSeasonDto;

    public ScraperManager(UaapSeasonDto uaapSeasonDto) {
        this.uaapSeasonDto = uaapSeasonDto;
        gameScraper = GameScraper.gameScraperFactory(uaapSeasonDto);
    }

    public UaapSeasonDto getUaapSeasonDtoToSave() {
        List<UaapGameDto> uaapGameDtosFromWeb = gameScraper.scrapeAllGamesAndResults();
        List<UaapGameDto> uaapGameDtosDb = uaapSeasonDto.getUaapGames();

//      -generate uaapGames missing (Web - DB)
        List<UaapGameDto> uaapGameDtosToAdd;
        //compares season, game num and final results
        uaapGameDtosToAdd = uaapGameDtosFromWeb.stream().filter(g -> !uaapGameDtosDb.contains(g)).toList();

//      map of UaapGameDb gameNumber and server gameNumber
        HashMap<Integer, Integer> gameIdMap = gameScraper.extractGameNumGameIdMap();
//
        Set<Integer> notFoundGames = new HashSet<>();
        for (UaapGameDto game : uaapGameDtosToAdd) {
            Integer gameNumWeb = gameIdMap.get(game.getGameNumber());
            String urlStats = uaapSeasonDto.getUrl().replace(":id",  String.valueOf(gameNumWeb));
            Document doc;
            try {
                doc = getDocumentStats(urlStats);
            } catch (IOException e) {
                notFoundGames.add(game.getGameNumber());
                continue;
            }

//            add additional data
            gameScraper.addAdditionalGameData(doc, game);

            //add player stats
            for (GameResultDto gameResult : game.getGameResults()) {
                List<PlayerStat> playerStats = gameScraper.scrapePlayerStats(gameResult, doc);
                gameResult.setPlayerStats(playerStats);
            }
        }

        return UaapSeasonDto.builder()
                .url(uaapSeasonDto.getUrl())
                .gameCode(uaapSeasonDto.getGameCode())
                .uaapGames(uaapGameDtosToAdd)
                .seasonNumber(uaapSeasonDto.getSeasonNumber())
                .isUrlWorking(uaapSeasonDto.isUrlWorking())
                .id(uaapSeasonDto.getId())
                .build();
    }


//    for getting stats (will try to fetch 3 times with 1 second of waiting time)
    private Document getDocumentStats(String urlStats) throws IOException {
        int maxAttempt = 3;
        int i = 0;
        Document doc;
        while (true) {
            try {
                doc = UtilityClass.getGameDocument(urlStats);
                return doc;
            } catch (HttpStatusException e) {
                if (e.getStatusCode() >= 500) {
                    if (i < 3) {
                        ++i;
                        try {
                            Thread.sleep(1000 * 1);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else {
                        throw e;
                    }
                }
            }
        }
    }
}
