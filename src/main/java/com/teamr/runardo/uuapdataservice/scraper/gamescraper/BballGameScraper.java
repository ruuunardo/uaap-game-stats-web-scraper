package com.teamr.runardo.uuapdataservice.scraper.gamescraper;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.data.entity.UaapTeam;
import com.teamr.runardo.uuapdataservice.scraper.statsfactory.BballPlayerStatFactory;
import com.teamr.runardo.uuapdataservice.scraper.statsfactory.PlayerStatsFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class BballGameScraper extends GameScraper {

    public BballGameScraper(UaapSeasonDto uaapSeasonDto) {
        super(uaapSeasonDto);
    }

    @Override
    protected Elements getGameSchedElements(Document gameDoc) {
        // get elements with the game data
        String gameSchedQuery = "a.schedule-box";      //anchor tag with "game-score class"
        Elements gameScheds = gameDoc.select(gameSchedQuery);
        return gameScheds;
    }

    @Override
    protected UaapGameDto getScrapeGame(Element gameSched) {
        int gameNum = Integer.parseInt(gameSched.attr("href").split("/+")[4]);  //"match 1" get match number 1

        UaapGameDto uaapGameDto = new UaapGameDto();
        uaapGameDto.setGameNumber(gameNum);
        uaapGameDto.setSeasonId(uaapSeasonDtofromDb.getId());
        return uaapGameDto;
    }

    @Override
    protected void mapGameResultsToGame(Element gameSched, UaapGameDto game) {
        Elements schedTeams = gameSched.select(".scheduled-team");

        GameResultDto homeTeam = extractGameResult(game, schedTeams, "HOME");
        GameResultDto awayTeam = extractGameResult(game, schedTeams, "AWAY");

        game.setGameResults(List.of(homeTeam, awayTeam));
    }


    private GameResultDto extractGameResult(UaapGameDto scrapeGame, Elements schedTeams, String teamTag) {
        int index = "HOME".equals(teamTag) ? 0 : 1;
        String data = schedTeams.get(index).text().replaceAll("\\s+", "").toUpperCase();
        String[] splitStr = data.split("(?<=\\D)(?=\\d)");

        UaapTeam uaapTeam = UaapTeam.parse(splitStr[0]);
        int finalScore = Integer.parseInt(splitStr[1]);

        GameResultDto build = new GameResultDto.GameResultDtoBuilder()
                .setGameId(scrapeGame.getId())
                .setTeamTag(teamTag)
                .setFinalScore(finalScore)
                .setUniv(UaapTeam.uaapUnivFactory(uaapTeam))
                .setId(scrapeGame, uaapSeasonDtofromDb)
                .build();
        return build;
    }

    @Override
    public void addAdditionalGameData(Document doc, UaapGameDto game) {


        Elements elements = doc.select("div#game-details div.game-detail span");
        String venue = elements.get(1).text();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a");
        String textDate = elements.get(2).text();
        LocalDateTime gameDateLDT = LocalDateTime.parse(textDate, formatter);

        game.setVenue(venue);
        game.setGameSched(gameDateLDT);

    }

    //------------------------ UTILITY METHOD ------------------------------//

    @Override
    public HashMap<Integer, Integer> extractGameNumGameIdMap() {
        //get game schedule elements from Document
        Elements gameScheds = getGameSchedElements(document);

        HashMap<Integer, Integer> webGameIdMap = new HashMap<>();

        for (Element gameSched : gameScheds) {
            String[] split = gameSched.attr("href").split("/+");    //get link and split
            int index = 4;
            int webGameId = Integer.parseInt(split[index]);

//            UaapGameDto game = getScrapeGame(gameSched);
//            webGameIdMap.put(game.getGameNumber(), webGameId);
            webGameIdMap.put(webGameId, webGameId); //same id
        }
        return  webGameIdMap;
    }

    //------------------------ FOR PLAYER STATS SCRAPER ------------------------------//
    @Override
    protected Elements getTableBodyElements(Document gameDocument) {
//        String cssQuery = "div#game-stats-container div.team-stats tbody";
        String cssQuery = "div.boxscorewrap table.box-score tbody:has(.bsheader_type)";
        Elements elements = gameDocument.select(cssQuery);
        System.out.println("/");
        return elements;
    }

    @Override
    protected List<PlayerStat> getPlayerStatList(List<String> playerStatCsvList, GameResultDto gameResultDto) {
        boolean isFirstFive = "".equals(playerStatCsvList.get(6));
        List<PlayerStat> playerStatList = new ArrayList<>();

        for (int i = 1; i < playerStatCsvList.size(); ++i) {
            String lineData = playerStatCsvList.get(i);
            if ("".equals(lineData)) {
                isFirstFive = !isFirstFive;
                continue;
            }
            lineData = isFirstFive ? "*".concat(lineData) : lineData;

            PlayerStatsFactory playerStatsFactory = getPlayerStatList();
            Optional<PlayerStat> playerStat = playerStatsFactory.parse(uaapSeasonDtofromDb, gameResultDto, lineData);
            playerStat.ifPresent(playerStatList::add);
        }
        return playerStatList;
    }

    @Override
    protected PlayerStatsFactory getPlayerStatList() {
        return new BballPlayerStatFactory();
    }

}
