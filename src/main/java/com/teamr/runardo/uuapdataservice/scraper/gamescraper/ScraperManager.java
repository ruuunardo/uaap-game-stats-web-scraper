package com.teamr.runardo.uuapdataservice.scraper.gamescraper;


import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScraperManager {
    private final GameScraper gameScraper;

    public ScraperManager(UaapSeasonDto uaapSeasonDto) {
        gameScraper = GameScraper.gameScraperFactory(uaapSeasonDto);
    }

    public UaapSeasonDto getUaapSeasonDto() {
        UaapSeasonDto uaapSeasonDto = gameScraper.generateUaapSeasonDtoToSave();

        //map of UaapGameDb gameNumber and server gameNumber
        HashMap<Integer, Integer> gameIdMap = gameScraper.extractGameNumGameIdMap();

        Set<Integer> notFoundGames = new HashSet<>();
        for (UaapGameDto game : uaapSeasonDto.getUaapGames()) {
            Integer gameNumWeb = gameIdMap.get(game.getGameNumber());
            String urlStats = gameScraper.getUaapSeasonDtofromDb().getUrl().replace(":id",  String.valueOf(gameNumWeb));
            Document doc;
            try {
                doc = getDocumentStats(urlStats);
            } catch (IOException e) {
                notFoundGames.add(game.getGameNumber());
                continue;
            }

            //add additional data
            gameScraper.addAdditionalGameData(doc, game);

            //add player stats
            for (GameResultDto gameResult : game.getGameResults()) {
                List<PlayerStat> playerStats = gameScraper.scrapeStats(gameResult, doc);
                gameResult.setPlayerStats(playerStats);
            }
        }
        return uaapSeasonDto;
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
