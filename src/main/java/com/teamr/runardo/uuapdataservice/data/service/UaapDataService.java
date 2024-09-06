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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UaapDataService {
    private UaapSeasonRepository uaapSeasonRepository;
    private UaapGameRepository uaapGameRepository;
    private BasketballPlayerStatRepository basketballPlayerStatRepository;
    private VolleyballPlayerStatRepository volleyballPlayerStatRepository;
    private PlayerStatRepository playerStatRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public UaapDataService(PlayerStatRepository playerStatRepository, UaapSeasonRepository uaapSeasonRepository, UaapGameRepository uaapGameRepository, BasketballPlayerStatRepository basketballPlayerStatRepository, VolleyballPlayerStatRepository volleyballPlayerStatRepository, PlayerRepository playerRepository) {
        this.uaapSeasonRepository = uaapSeasonRepository;
        this.uaapGameRepository = uaapGameRepository;
        this.basketballPlayerStatRepository = basketballPlayerStatRepository;
        this.volleyballPlayerStatRepository = volleyballPlayerStatRepository;
        this.playerRepository = playerRepository;
        this.playerStatRepository = playerStatRepository;

    }

    public List<UaapSeason> findAllUaapSeason() {
        return uaapSeasonRepository.findAll();
    }

    public void addUaapSeason(UaapSeason uaapSeason) {
        uaapSeasonRepository.customSaveGame(uaapSeason);
    }

    public UaapSeason findUaapSeasonById(Integer id) {
        return uaapSeasonRepository.findById(id).get();
    }

    public void deleteUaapSeasonById(int seasonId) {
        uaapSeasonRepository.deleteById(seasonId);
    }

    public void deleteUaapGames(Optional<List<Integer>> selections) {
        if (selections.isPresent()) {
            uaapGameRepository.deleteAllById(selections.get());
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
            game.setUrlWorking(true);
        } catch (IOException e) {
            game.setUrlWorking(false);
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
    public void updateUaapSeasonGames(int id) {
        //get Uaap Season
        Optional<UaapSeason> uaapSeason = uaapSeasonRepository.findById(id);
        if (uaapSeason.isPresent()) {
            ScraperManager scraperManager = new ScraperManager(UaapSeasonDto.convertToDto(uaapSeason.get()));
            UaapSeasonDto uaapSeasonDto = scraperManager.getUaapSeasonDto();

            List<Player> playerList = playerRepository.findAll();
            for (UaapGameDto uaapGameDto : uaapSeasonDto.getUaapGames()) {
                UaapGame uaapGame = UaapGameDto.convertToEntity(uaapGameDto);
                uaapGameRepository.save(uaapGame);

                for (GameResultDto gameResultDto : uaapGameDto.getGameResults()) {
                    List<PlayerStat> playerStats = gameResultDto.getPlayerStats();
                    savePlayerStat(playerStats, playerList, uaapSeasonDto.getGameCode().getGameCode());
                }
            }
        }
    }

    private void savePlayerStat(List<PlayerStat> playerStats, List<Player> playerList, String gameCode) {
        for (PlayerStat playerStat : playerStats) {
//            if (gameCode.endsWith("BB")) {
//                BasketballPlayerStat stat = (BasketballPlayerStat) playerStat;
//                if (!playerList.contains(stat.getPlayer())) {
//                    playerRepository.save(stat.getPlayer());
//                }
//                basketballPlayerStatRepository.save(stat);
//            } else if (gameCode.endsWith("VB")) {
//                VolleyballPlayerStat stat = (VolleyballPlayerStat) playerStat;
//                if (!playerList.contains(stat.getPlayer())) {
//                    playerRepository.save(stat.getPlayer());
//                }
//                volleyballPlayerStatRepository.save(stat);
//            }
            playerStatRepository.save(playerStat);
        }
    }


    public Optional<List<UaapGame>> findUaapGamesBySeasonId(int seasonNumber) {
        return uaapGameRepository.findAllBySeasonId(seasonNumber);
    }
}
