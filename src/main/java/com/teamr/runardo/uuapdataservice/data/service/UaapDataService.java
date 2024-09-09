package com.teamr.runardo.uuapdataservice.data.service;

import com.teamr.runardo.uuapdataservice.data.entity.*;
import com.teamr.runardo.uuapdataservice.data.repository.*;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.scraper.gamescraper.ScraperManager;
import com.teamr.runardo.uuapdataservice.scraper.gamescraper.UtilityClass;
import com.teamr.runardo.uuapdataservice.scraper.statsfactory.CsvGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UaapDataService {
    private UaapSeasonRepository uaapSeasonRepository;
    private UaapGameRepository uaapGameRepository;
    private PlayerStatRepository playerStatRepository;
    private GameResultRepository gameResultRepository;
    private PlayerRepository playerRepository;

    public UaapDataService(UaapSeasonRepository uaapSeasonRepository, UaapGameRepository uaapGameRepository, PlayerStatRepository playerStatRepository, GameResultRepository gameResultRepository, PlayerRepository playerRepository) {
        this.uaapSeasonRepository = uaapSeasonRepository;
        this.uaapGameRepository = uaapGameRepository;
        this.playerStatRepository = playerStatRepository;
        this.gameResultRepository = gameResultRepository;
        this.playerRepository = playerRepository;
    }

    public List<UaapSeason> findAllUaapSeason() {
        return uaapSeasonRepository.findAll();
    }

    public void mergeUaapSeason(UaapSeason uaapSeason) {
        uaapSeasonRepository.customSaveGame(uaapSeason);
    }

    public UaapSeason findUaapSeasonById(Integer id) {
        UaapSeason uaapSeason = uaapSeasonRepository.findById(id).get();
        List<UaapGame> uaapGames = uaapSeason.getUaapGames();

        Comparator<UaapGame> compareByGameNum = new Comparator<UaapGame>() {
            @Override
            public int compare(UaapGame o1, UaapGame o2) {
                return Integer.compare(o1.getGameNumber(), o2.getGameNumber());
            }
        };

        uaapGames.sort(compareByGameNum);
        return uaapSeason;
    }

    public void deleteUaapSeasonById(int seasonId) {
        Optional<UaapSeason> season = uaapSeasonRepository.findById(seasonId);
        UaapSeason uaapSeason = season.get();
        if (uaapSeason.getUaapGames() != null) {
            for (UaapGame g : uaapSeason.getUaapGames()) {
                for (GameResult r : g.getGameResults()) {
                    playerStatRepository.deleteAllByGameResultId(r.getId(), uaapSeason.getGameCode().getGameCode());
                }
            }
        }

        uaapSeasonRepository.delete(season.get());
    }

    public void deleteUaapGamesByIds(Optional<List<Integer>> selections, String gameCode) {
        if (selections.isPresent()) {
            for (Integer i : selections.get()) {
                deleteUaapGameByGameId(i, gameCode);
            }
        }
    }
//
    public void uploadUaapSeason(List<UaapSeason> uaapSeasonList) {
        List<UaapSeason> allUaapSeason = uaapSeasonRepository.findAll();
        HashMap<String, Boolean> uaapSeasonCache = cacheAllUaapSeason(allUaapSeason);

        uaapSeasonList.stream()
                .filter(s -> {
                    return !(uaapSeasonCache.getOrDefault(getUaapSeasonCode(s), false));
                })
                .map(this::checkUaapSeasonUrl)
                .forEach(uaapSeason -> {
                    uaapSeasonRepository.customSaveGame(uaapSeason);
                });
    }

    private HashMap<String, Boolean> cacheAllUaapSeason(List<UaapSeason> allUaapSeason) {
        HashMap<String, Boolean> uaapSeasonCache = new HashMap<>();
        allUaapSeason.stream().parallel()
                .forEach(g -> {
                    uaapSeasonCache.put(getUaapSeasonCode(g), true);
                    }
                );
        return uaapSeasonCache;
    }

    private String getUaapSeasonCode(UaapSeason g) {
        return String.join("-", g.getGameCode().getGameCode(), String.valueOf(g.getSeasonNumber()));
    }

//    url check----------------------------------------------------------
    public UaapSeason checkUaapSeasonUrl(UaapSeason game) {
        try {
            UtilityClass.getGameDocument(game.getUrl());
            game.setUrlWork(true);
        } catch (IOException e) {
            game.setUrlWork(false);
        }
        return game;
    }

//    export CSV---------------------------------------------------------
    public void generateCSV(HttpServletResponse response, String id) throws IOException {
        UaapSeason season = findUaapSeasonById(Integer.parseInt(id));
        List<UaapGame> uaapGames = season.getUaapGames();
        List<UaapGameDto> uaapGameDtos = uaapGames.stream()
                .map(UaapGameDto::convertToDto)
                .toList();

        response.setContentType("text/csv");
        String header = String.format("attachment; filename=\"uaap-games_%s-%s.csv\"", season.getGameCode().getGameCode(), season.getSeasonNumber());
        response.addHeader("Content-Disposition", header);

        CsvGenerator csvGenerator = new CsvGenerator(response.getWriter());
        csvGenerator.writeUaapGamesToCsv(uaapGameDtos, season);
    }

    public void generateCSV(HttpServletResponse response, String id, Optional<List<Integer>> selections) throws IOException {
        if (selections.isPresent()) {
            UaapSeason season = findUaapSeasonById(Integer.parseInt(id));
            List<UaapGameDto> uaapGameDtos = getUaapGameDtos(selections, season);

            response.setContentType("text/csv");
            String header = String.format("attachment; filename=\"uaap-games_%s-%s_filtered.csv\"", season.getGameCode().getGameCode(), season.getSeasonNumber());
            response.addHeader("Content-Disposition", header);

            CsvGenerator csvGenerator = new CsvGenerator(response.getWriter());
            csvGenerator.writeUaapGamesToCsv(uaapGameDtos, season, selections);
        }
    }
    private List<UaapGameDto> getUaapGameDtos(Optional<List<Integer>> selections, UaapSeason season) {
        List<UaapGame> uaapGames = uaapGameRepository.findAllById(selections.get());
        List<UaapGameDto> uaapGameDtos = uaapGames.stream()
                .map(UaapGameDto::convertToDto)
                .toList();

        String gameCode = season.getGameCode().getGameCode();

        for (UaapGameDto g : uaapGameDtos) {
            for (GameResultDto gameResultDto : g.getGameResults()) {
                Optional<List<PlayerStat>> playerStats = Optional.empty();
                playerStats = getPlayerStats(gameResultDto, gameCode);
                playerStats.ifPresent(gameResultDto::setPlayerStats);
            }
        }
        return uaapGameDtos;
    }

    private Optional<List<PlayerStat>> getPlayerStats(GameResultDto gameResultDto, String gameCode) {
        Optional<List<PlayerStat>> playerStats = Optional.empty();
        playerStats = playerStatRepository.findAllByGameResult(gameResultDto.getId(), gameCode);
        return playerStats;
    }

//    update Uaap Games---------------------------------------------------
    public void uaapGames() {

    }

    public void updateUaapSeasonGamesById(int id) {
        //get Uaap Season
        Optional<UaapSeason> uaapSeason = uaapSeasonRepository.findById(id);
        if (uaapSeason.isPresent()) {
            //convert to dto
            UaapSeasonDto uaapSeasonDtoDb = UaapSeasonDto.convertToDto(uaapSeason.get());
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
                    System.out.println(uaapGame);
                    deleteUaapGameByGameNumAndSeasonId(uaapGame.getGameNumber(),uaapGame.getSeasonId(), uaapSeasonDtoToUpdate.getGameCode().getGameCode());
                    System.out.println("Game deleted" + uaapGame);
                }

                int gameId = uaapGameRepository.save(uaapGame).getId();//saves UaapGame and GameResult
                for (GameResultDto gameResultDto : uaapGameDto.getGameResults()) {
                    GameResult gameResult = GameResultDto.convertToEntity(gameResultDto);
                    gameResult.setGameId(gameId);
                    gameResultRepository.save(gameResult);
                    //delete player stats first

                    for (PlayerStat playerStat : gameResultDto.getPlayerStats()) {
                        playerStatRepository.save(playerStat);  //saves Stats and Players
                    }
                }
            }
        }
    }

    private void deleteUaapGameByGameNumAndSeasonId(int gameNumber, int seasonId, String gameCode) {
        //find Game
        Optional<UaapGame> game = uaapGameRepository.findAllBySeasonIdAndGameNumber(seasonId, gameNumber);
        game.ifPresent(uaapGame -> deleteUaapGame(gameCode, uaapGame));
        deleteUaapGame(gameCode, game.get());
    }

    private void deleteUaapGameByGameId(int id, String gameCode) {
        //find Game
        Optional<UaapGame> game = uaapGameRepository.findById(id);
        game.ifPresent(uaapGame -> deleteUaapGame(gameCode, uaapGame));
        deleteUaapGame(gameCode, game.get());
    }

    private void deleteUaapGame(String gameCode, UaapGame uaapGame) {
        for (GameResult gr : uaapGame.getGameResults()) {
            String id = gr.getId();
            //delete PlayerStats
            playerStatRepository.deleteAllByGameResultId(id, gameCode);
        }
        //delete game and gameresult
        uaapGameRepository.delete(uaapGame);
    }

    public Optional<List<UaapGame>> findUaapGamesBySeasonId(int seasonNumber) {
        return uaapGameRepository.findAllBySeasonId(seasonNumber);
    }
}
