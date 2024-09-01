package com.teamr.runardo.uuapdataservice.data.controller;


import com.teamr.runardo.uuapdataservice.data.entity.GameResult;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.data.entity.UaapUniv;
import com.teamr.runardo.uuapdataservice.data.service.UaapDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class UaapDataController {

    @Autowired
    private UaapDataService uaapDataService;

    @GetMapping()
    public List<UaapSeason> getAllSeason() {
        List<UaapSeason> uaapSeasons = uaapDataService.findAllUaapSeason();
        return uaapSeasons;
    }

    @GetMapping("{seasonId}")
    public UaapSeason getUaapSeason(@PathVariable int seasonId) {
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonById(seasonId);
        return uaapSeason;
    }

    @PostMapping()
    public void addUaapSeason(@RequestBody UaapSeason uaapSeason) {
        uaapDataService.addUaapSeason(uaapSeason);
    }

//    @GetMapping("/univ-list")
//    public List<UaapUniv> getAllUniv() {
//        List<UaapUniv> univList = uaapDataService.findAllUaapUniv();
//        return univList;
//    }
//
//    @GetMapping("/game-result/{gameId}")
//    public List<GameResult> getAllUniv(@PathVariable int gameId) {
//        List<GameResult> gameResults = uaapDataService.findAllGameResultsbyGameId(gameId);
//        return gameResults;
//    }

}
