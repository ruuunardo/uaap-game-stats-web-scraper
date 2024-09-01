package com.teamr.runardo.uuapdataservice.data.service;


import com.teamr.runardo.uuapdataservice.data.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.data.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.data.entity.BasketballPlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import com.teamr.runardo.uuapdataservice.data.repository.BasketballPlayerStatRepository;
import com.teamr.runardo.uuapdataservice.data.repository.UaapGameRepository;
import com.teamr.runardo.uuapdataservice.data.repository.UaapSeasonRepository;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.data.repository.VolleyballPlayerStatRepository;
import com.teamr.runardo.uuapdataservice.filerepository.FileStorageRepository;
import com.teamr.runardo.uuapdataservice.scraper.CsvGenerator;
import com.teamr.runardo.uuapdataservice.scraper.UtilityClass;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UaapDataService {
    private UaapSeasonRepository uaapSeasonRepository;

    private UaapGameRepository uaapGameRepository;

    private BasketballPlayerStatRepository basketballPlayerStatRepository;

    private VolleyballPlayerStatRepository volleyballPlayerStatRepository;

    private FileStorageRepository fileStorageRepository;


    @Autowired
    public UaapDataService(UaapSeasonRepository uaapSeasonRepository, UaapGameRepository uaapGameRepository, BasketballPlayerStatRepository basketballPlayerStatRepository, VolleyballPlayerStatRepository volleyballPlayerStatRepository, FileStorageRepository fileStorageRepository) {
        this.uaapSeasonRepository = uaapSeasonRepository;
        this.uaapGameRepository = uaapGameRepository;
        this.basketballPlayerStatRepository = basketballPlayerStatRepository;
        this.volleyballPlayerStatRepository = volleyballPlayerStatRepository;
        this.fileStorageRepository = fileStorageRepository;
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

    public ResponseEntity<Resource> getImageResource(String resource) {
        String imgFile = resource.concat(".png");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename = \"%s\"", imgFile))
                .body(fileStorageRepository.findByName(imgFile));
    }
//    import CSV--------------------------------------------------

    public void importCSV(InputStream inputStream) {
        List<UaapSeason> allUaapSeason = uaapSeasonRepository.findAll();
        HashMap<String, Boolean> uaapSeasonCache = cacheAllUaapSeason(allUaapSeason);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        bufferedReader.lines()
                .skip(1)
                .map(UaapSeason::parse)
                .filter(us -> {
                    return !(uaapSeasonCache.getOrDefault(getUaapSeasonCode(us), false));
                })
                .map(this::checkUaapSeasonUrl)
                .forEach(System.out::println);
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
//    url check--------------------------------------------------

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

        for (UaapGameDto g : uaapGameDtos) {
            for (GameResultDto gameResultDto : g.getGameResults()) {
                Optional<List<PlayerStat>> playerStats = Optional.empty();
                if(season.getGameCode().getGameCode().endsWith("BB")) {
                    playerStats = basketballPlayerStatRepository.findAllByGameResult(gameResultDto.getId());
                } else if (season.getGameCode().getGameCode().endsWith("VB")) {
                    playerStats = volleyballPlayerStatRepository.findAllByGameResult(gameResultDto.getId());
                }
                playerStats.ifPresent(gameResultDto::setPlayerStats);
            }
        }
        return uaapGameDtos;
    }
}
