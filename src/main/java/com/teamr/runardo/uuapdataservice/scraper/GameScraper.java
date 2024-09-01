//package com.teamr.runardo.uuapdataservice.Scraper;
//
//
//import com.teamr.runardo.uuapdataservice.Data.entity.UaapGame;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public abstract class GameScraper<T> {
//
//    protected String gameCode;
//    protected Document document;
//    protected String url;
//    protected int seasonNumber;
//    private Elements gameScheds;
//    private List<UaapGame> games;
//
//
//    public GameScraper(String gameCode, String url, int seasonNumber) {
//        this.gameCode = gameCode;
//        this.url = url;
//        this.seasonNumber = seasonNumber;
//        try {
//            this.document = UtilityClass.getGameDocument(url);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //------------------- SCRAPE ALL UAAP GAMES w/ Game Results (home and away) ----------------------//
//    //ScrapeAllGames
//    public List<UaapGame> scrapeAllGames() {
//        //Step1: get game schedule elements from Document
//        gameScheds = getGameSchedElements(document);
//
//        //Step2: iterate each element to extract each game data
//        games = new ArrayList<>();
//        for (Element gameSched : gameScheds) {
//            //Step2.1: Game details and uaap game
//            UaapGame scrapedGame = extractScrapeGame(gameSched);
//
//            //Step2.2: extract game results and add to scrapedGame
//            extractGameResultsToGame(gameSched, scrapedGame);
//
//            //add to game list
//            games.add(scrapedGame);
//        }
//
//        //Step3: return games
//        return games;
//    }
//
//    //ScrapeAllGames: Step1
//    protected abstract Elements getGameSchedElements(Document gameDoc);
//
//    //ScrapeAllGames: Step2.1
//    protected abstract UaapGame extractScrapeGame(Element gameSched);
//
//    //ScrapeAllGames: Step2.2
//    protected abstract void extractGameResultsToGame(Element gameSched, UaapGame scrapeGame);
//
//    //------------------- SCRAPE STATS FOR EACH GAME ----------------------//
//    //Scrape stats for each game
//
//    public List<T> scrapeStats(GameResult gameResult, Document gameDocument){
//        //Player stats table elements (HOME, AWAY)
//        Elements playerStatTables = getTableElements(gameDocument);
//
//        //--get table element
//        Element teamTableElement = "HOME".equals(gameResult.getTeamTag()) ? playerStatTables.get(0) : playerStatTables.get(1);
//
//        //--convert to csv list
//        List<String> playerStatCsvList = teamTableElement.select("tr")
//                .stream()
//                .map(tr ->
//                        tr.select("td")                 //convert each players data to csv
//                                .stream()
//                                .map(Element::text)
//                                .map(str -> str.replace(",", ""))
//                                .collect(Collectors.joining(","))
//                )
//                .toList();
//
//        //extract player stats
//        List<T> playerStatList = new ArrayList<>();
//        for(String lineData: playerStatCsvList){
//            PlayerStatBuilder playerStatBuilder = getBuilder();
//            Optional<T> playerStat = playerStatBuilder.parse(gameResult, lineData);
//            playerStat.ifPresent(playerStatList::add);
//        }
//
//        return playerStatList;
//    }
//    protected abstract PlayerStatBuilder getBuilder();
//
//    protected abstract Elements getTableElements(Document gameDocument);
//
//    //------------------- Utility methods ----------------------//
//
//    public abstract HashMap<Integer, Integer> extractGameNumGameIdMap();
//
//    public abstract void addAdditionalGameData(Document doc, UaapGame game);
//
//    public abstract void setPlayerStatsToGameResult(GameResult gameResult, List<T> playerStatsToGameResult);
//
//    //------------------- GETTER and SETTER ----------------------//
//
//    public String getGameCode() {
//        return gameCode;
//    }
//
//    public void setGameCode(String gameCode) {
//        this.gameCode = gameCode;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public int getSeasonNumber() {
//        return seasonNumber;
//    }
//
//    public void setSeasonNumber(int seasonNumber) {
//        this.seasonNumber = seasonNumber;
//    }
//
//    //------------------- SIMPLE FACTORY ----------------------//
//
//    public static GameScraper gameScraperFactory(String gameCode, int seasonNumber) {
//        if (gameCode.endsWith("BB")) {
//            String url = "https://uaap.livestats.ph/tournaments/uaap-season-:SEASON-:CATEGORY?game_id=:id";
//            String category = switch (gameCode) {
//                case "MBB" -> "men-s";
//                case "WBB" -> "women-s";
//                default -> throw new IllegalStateException("Unexpected value: " + gameCode);
//            };
//
//            return new BballGameScraper(gameCode, url.replace(":CATEGORY", category).replace(":SEASON", String.valueOf(seasonNumber)), seasonNumber);
//        } else if (gameCode.endsWith("VB")) {
//            String url  = "https://uaapvolleyball.livestats.ph/tournaments/uaap-volleyball-season-:SEASON-:CATEGORY?game_id=:id";;
//            String category = switch (gameCode) {
//                case "MVB" -> "men-s";
//                case "WVB" -> "women-s";
//                default -> throw new IllegalStateException("Unexpected value: " + gameCode);
//            };
//
//            return new VballGameScraper(gameCode, url.replace(":CATEGORY", category).replace(":SEASON", String.valueOf(seasonNumber)), seasonNumber);
//        }
//
//        throw new RuntimeException("Game Code error: " + gameCode);
//    }
//
//    public static GameScraper gameScraperFactory(UaapSeason uaapSeason) {
//        String gameCode = uaapSeason.getGameCode().getGameCode();
//        if (gameCode.endsWith("BB")) {
//            return new BballGameScraper(gameCode, uaapSeason.getUrl(), uaapSeason.getSeasonNumber());
//        } else if (gameCode.endsWith("VB")) {
//            return new VballGameScraper(gameCode, uaapSeason.getUrl(), uaapSeason.getSeasonNumber());
//        }
//        throw new RuntimeException("Game Code error: " + gameCode);
//    }
//}
