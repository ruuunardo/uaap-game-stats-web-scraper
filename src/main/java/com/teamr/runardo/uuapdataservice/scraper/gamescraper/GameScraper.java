package com.teamr.runardo.uuapdataservice.scraper.gamescraper;


import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.scraper.statsfactory.PlayerStatsFactory;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class GameScraper {

    protected Document document;
    protected UaapSeasonDto uaapSeasonDtofromDb;

//    Set document(using the URL) and uaapSeasonDto from UaapSeasonDto
    public GameScraper(UaapSeasonDto uaapSeasonDto) {
        this.uaapSeasonDtofromDb = uaapSeasonDto;
        if (uaapSeasonDto.isUrlWorking()) {
            try {
                this.document = UtilityClass.getGameDocument(uaapSeasonDto.getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public UaapSeasonDto generateUaapSeasonDtoToSave() {
//     -get games from DB and Web
        List<UaapGameDto> uaapGameDtosFromWeb = scrapeAllGames();
        List<UaapGameDto> uaapGamesDb = uaapSeasonDtofromDb.getUaapGames();

//        -generate uaapGames missing (Web - DB)
        List<UaapGameDto> uaapGameDtosToAdd;
        uaapGameDtosToAdd = uaapGameDtosFromWeb.stream().filter(g -> !uaapGamesDb.contains(g)).toList();

//        -set UaapGamesSeasonToSave
        return UaapSeasonDto.builder()
                .url(uaapSeasonDtofromDb.getUrl())
                .gameCode(uaapSeasonDtofromDb.getGameCode())
                .uaapGames(uaapGameDtosToAdd)
                .seasonNumber(uaapSeasonDtofromDb.getSeasonNumber())
                .isUrlWorking(uaapSeasonDtofromDb.isUrlWorking())
                .id(uaapSeasonDtofromDb.getId())
                .build();
    }

//------------------- SCRAPE ALL UAAP GAMES w/ Game Results (home and away) ----------------------//
    //ScrapeAllGames then assigned to uaapGameDtos
    protected List<UaapGameDto> scrapeAllGames() {
        //Step1: get game schedule elements from Document for Uaap Game extraction
        Elements gameScheds = getGameSchedElements(document);

        //Step2: iterate each element to extract each game data
        List<UaapGameDto> games = new ArrayList<>();
        for (Element gameSched : gameScheds) {
            //Step2.1: Game details and uaap game
            UaapGameDto scrapedGame = extractScrapeGame(gameSched);

            //Step2.2: extract game results and add to scrapedGame
            extractGameResultsToGame(gameSched, scrapedGame);

            //add to game list
            games.add(scrapedGame);
        }

        //Step3: return games
        return games;
    }

    //ScrapeAllGames: Step1
    protected abstract Elements getGameSchedElements(Document gameDoc);

    //ScrapeAllGames: Step2.1
    protected abstract UaapGameDto extractScrapeGame(Element gameSched);

    //ScrapeAllGames: Step2.2
    protected abstract void extractGameResultsToGame(Element gameSched, UaapGameDto scrapeGame);



//    ------------------- SCRAPE STATS FOR EACH GAME ----------------------//
    //Scrape stats for each game
    public List<PlayerStat> scrapeStats(GameResultDto gameResultDto, Document gameDocument){
        //Player stats table BODY elements (HOME, AWAY) - should be size 2
        Elements playerStatTables = getTableElements(gameDocument);

        //--get table element if Home or Away (index0-> Home, index1-> Away)
        Element teamTableElement = "HOME".equals(gameResultDto.getTeamTag()) ? playerStatTables.get(0) : playerStatTables.get(1);

        //--get all row elements and convert to csv list
        List<String> playerStatCsvList = teamTableElement.select("tr")
                .stream()
            .map(tr ->                          //for each row convert each players data to csv
                        tr.select("td")
                                .stream()
                                .map(Element::text)
                                .map(str -> str.replace(",", ""))
                                .collect(Collectors.joining(","))
                )
                .toList();

        //extract player stats
        List<PlayerStat> playerStatList = new ArrayList<>();
        for(String lineData: playerStatCsvList){
            PlayerStatsFactory playerStatsFactory = getFactory();
            Optional<PlayerStat> playerStat = playerStatsFactory.parse(uaapSeasonDtofromDb, gameResultDto, lineData);
            playerStat.ifPresent(playerStatList::add);
        }

        return playerStatList;
    }

    protected abstract PlayerStatsFactory getFactory();

    protected abstract Elements getTableElements(Document gameDocument);

    //------------------- Utility methods ----------------------//

    public abstract HashMap<Integer, Integer> extractGameNumGameIdMap();

    public abstract void addAdditionalGameData(Document doc, UaapGameDto game);


 //------------------- SIMPLE FACTORY ----------------------////
    public static GameScraper gameScraperFactory(UaapSeasonDto uaapSeasonDto) {
        String gameCode = uaapSeasonDto.getGameCode().getGameCode();
        if (gameCode.endsWith("BB")) {
            return new BballGameScraper(uaapSeasonDto);
        } else if (gameCode.endsWith("VB")) {
            return new BballGameScraper(uaapSeasonDto);
        }
        throw new RuntimeException("Game Code error: " + gameCode);
    }
}
