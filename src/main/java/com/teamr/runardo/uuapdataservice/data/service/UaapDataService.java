package com.teamr.runardo.uuapdataservice.data.service;

import com.teamr.runardo.uuapdataservice.data.entity.*;
import com.teamr.runardo.uuapdataservice.data.repository.*;
import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.utility.UtilityClass;
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

    public UaapSeason findUaapSeasonByIdSortedGames(Integer id) {
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

        //delete player stats first
        if (uaapSeason.getUaapGames() != null) {
            for (UaapGame g : uaapSeason.getUaapGames()) {
                for (GameResult r : g.getGameResults()) {
                    playerStatRepository.deleteAllStatsByGameResultId(r.getId(), uaapSeason.getGameCode().getGameCode());
                }
            }
        }

        //delete uaap season, uaap games, game result
        uaapSeasonRepository.delete(uaapSeason);
    }

    public void deleteUaapGamesByIds(Optional<List<Integer>> selections, String gameCode) {
        if (selections.isPresent()) {
            for (Integer i : selections.get()) {
                deleteUaapGameByGameId(i, gameCode);
            }
        }
    }

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

    public void deleteUaapGameByGameNumAndSeasonId(int gameNumber, int seasonId, String gameCode) {
        //find Game
        Optional<UaapGame> game = uaapGameRepository.findAllBySeasonIdAndGameNumber(seasonId, gameNumber);
        game.ifPresent(uaapGame -> deleteUaapGame(gameCode, uaapGame));
    }

    public void deleteUaapGameByGameId(int id, String gameCode) {
        //find Game
        Optional<UaapGame> game = uaapGameRepository.findById(id);
        game.ifPresent(uaapGame -> deleteUaapGame(gameCode, uaapGame));
        deleteUaapGame(gameCode, game.get());
    }

    public void deleteUaapGame(String gameCode, UaapGame uaapGame) {
        for (GameResult gr : uaapGame.getGameResults()) {
            String id = gr.getId();
            //delete PlayerStats
            playerStatRepository.deleteAllStatsByGameResultId(id, gameCode);
        }
        //delete game and gameresult
        uaapGameRepository.delete(uaapGame);
    }

    public Optional<List<UaapGame>> findUaapGamesBySeasonId(int seasonNumber) {
        return uaapGameRepository.findAllBySeasonId(seasonNumber);
    }

    public UaapGame saveUaapGame(UaapGame uaapGame) {
        return uaapGameRepository.save(uaapGame);
    }

    public GameResult saveGameResult(GameResult gameResult) {
        return gameResultRepository.save(gameResult);
    }

    public void savePlayerStat(PlayerStat playerStat) {
        playerStatRepository.save(playerStat);
    }

    public Optional<List<PlayerStat>> getPlayerStats(GameResultDto gameResultDto, String gameCode) {
        Optional<List<PlayerStat>> playerStats = Optional.empty();
        playerStats = playerStatRepository.findAllByGameResult(gameResultDto.getId(), gameCode);
        return playerStats;
    }

    public List<UaapGame> findAllUaapGamesById(List<Integer> integers) {
        return uaapGameRepository.findAllById(integers);
    }

    public List<UaapGameDto> getUaapGameDtos(List<UaapGame> uaapGames, String gameCode) {
        List<UaapGameDto> uaapGameDtos = uaapGames.stream()
                .map(UaapGameDto::convertToDto)
                .toList();

        for (UaapGameDto g : uaapGameDtos) {
            for (GameResultDto gameResultDto : g.getGameResults()) {
                Optional<List<PlayerStat>> playerStats = Optional.empty();
                playerStats = getPlayerStats(gameResultDto, gameCode);
                playerStats.ifPresent(gameResultDto::setPlayerStats);
            }
        }
        return uaapGameDtos;
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




}
