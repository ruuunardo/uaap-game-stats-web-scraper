package com.teamr.runardo.uuapdataservice.scraper.gamescraper;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGameCode;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameScraperTest {

    @Test
    void scrapeStats() throws IOException {
        UaapSeasonDto mbb = UaapSeasonDto.builder()
                .url("https://uaap.livestats.ph/tournaments/uaap-season-87-men-s?game_id=:id")
//                .id(38)
                .gameCode(new UaapGameCode("MBB", "Men's Basketball"))
                .isUrlWorking(true)
                .seasonNumber(87)
                .build();


        GameScraper bballGameScraper = new BballGameScraper(mbb);
        UaapGameDto uaapGameDtoTest = bballGameScraper.scrapeAllGamesAndResults().get(0);

        HashMap<Integer, Integer> gameIdMap = bballGameScraper.extractGameNumGameIdMap();
        String i = String.valueOf(gameIdMap.get(uaapGameDtoTest.getGameNumber()));
        String urlStats = mbb.getUrl().replace(":id", i);
        Document gameDocument = UtilityClass.getGameDocument(urlStats);

        bballGameScraper.addAdditionalGameData(gameDocument, uaapGameDtoTest);
        assertNotNull(uaapGameDtoTest.getVenue());
        assertNotNull(uaapGameDtoTest.getGameSched());

        List<PlayerStat> playerStats = bballGameScraper.scrapePlayerStats(uaapGameDtoTest.getGameResults().get(0), gameDocument);
//        playerStats.stream().forEach(System.out::println);
        assertNotNull(playerStats);
        assertTrue(playerStats.size() > 0);
    }

    @Test
    void test() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a");
        String textDate = "09/08/24 06:30 PM";

        LocalDateTime gameDateLDT = LocalDateTime.parse(textDate, formatter);
        System.out.println(gameDateLDT);

    }
}