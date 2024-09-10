package com.teamr.runardo.uuapdataservice.view;

import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGameCode;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.data.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.scraper.service.ScraperService;
import com.teamr.runardo.uuapdataservice.data.service.UaapDataService;
import com.teamr.runardo.uuapdataservice.scraper.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("uaap-games")
public class ViewController {
    private UaapDataService uaapDataService;
    private FileService fileService;
    private ScraperService scraperService;

    @Autowired
    public ViewController(UaapDataService uaapDataService, FileService fileService, ScraperService scraperService) {
        this.uaapDataService = uaapDataService;
        this.fileService = fileService;
        this.scraperService = scraperService;
    }

    //Uaap season list
    @GetMapping()
    public String getAllUaapSeasons(Model model) {
        List<UaapSeason> allUaapSeasonGames = uaapDataService.findAllUaapSeason();
        model.addAttribute("uaapSeasons", allUaapSeasonGames);
        return "uaap-season-list";
    }

    //Delete UaapSeason
    @GetMapping("/delete")
    public String deleteUaapSeasonGame(@RequestParam("gameSeasonId") int seasonId) {
        uaapDataService.deleteUaapSeasonById(seasonId);
        return "redirect:/uaap-games";
    }

    //Update game Season
    @GetMapping("/update")
    public String updateUaapGameSeason(@RequestParam("gameSeasonId") int id, Model model) {
        scraperService.updateUaapSeasonGamesById(id);
        return "redirect:/uaap-games";
    }

    //form for adding season
    @GetMapping("/show-form")
    public String showFormForAdd(Model model) {
        UaapSeason uaapSeason = new UaapSeason();
        uaapSeason.setGameCode(new UaapGameCode());
        model.addAttribute("gameSeason", uaapSeason);
        return "uaap-game-form";
    }

    //edit season
    @GetMapping("/edit")
    public String editUaapGame(@RequestParam("gameSeasonId") int id, Model model) {
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(id);
        model.addAttribute("gameSeason", uaapSeason);
        return "uaap-game-form";
    }

    //save season
    @PostMapping("/save")
    public String saveUaapGame(@Valid @ModelAttribute("gameSeason") UaapSeason uaapSeason, Errors errors) {
        if (!errors.hasErrors()) {
            Optional<List<UaapGame>> uaapGamesBySeasonId = uaapDataService.findUaapGamesBySeasonId(uaapSeason.getId());
            uaapGamesBySeasonId.ifPresent(uaapSeason::setUaapGames);
            uaapDataService.mergeUaapSeason(uaapSeason);
            return "redirect:/uaap-games";
        }
        return "uaap-games-form";
    }

    //save season from file
    @PostMapping(value = "/save-from-file")
    public String saveUaapGames(@RequestParam MultipartFile csvFile) throws IOException {
        List<UaapSeason> uaapSeasonList = fileService.getUaapSeasonList(csvFile);
        uaapDataService.uploadUaapSeason(uaapSeasonList);
        return "redirect:/uaap-games";
    }


    //Games and result
    @GetMapping("/gamelist/{gameSeasonId}")
    public String getGames(@PathVariable("gameSeasonId") int seasonId, Model model) {
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(seasonId);
        model.addAttribute("uaapSeason", uaapSeason);
        return "uaap-game-table";
    }

    //Delete games and result
    @GetMapping(value = "/gamelist/{gameSeasonId}", params = "delete=true")
    public String deleteGames(@PathVariable("gameSeasonId") int seasonId, @RequestParam("selections") Optional<List<Integer>> selections) {
        UaapSeason season = uaapDataService.findUaapSeasonByIdSortedGames(seasonId);
        uaapDataService.deleteUaapGamesByIds(selections, season.getGameCode().getGameCode());
        String path = "redirect:gameSeasonId";
        return path.replace("gameSeasonId", String.valueOf(seasonId));
    }

    //export to csv (selected Uaap Games)
    @GetMapping(value = "/gamelist/{gameSeasonId}", params = "download=true")
    public void downloadGameList(@PathVariable("gameSeasonId") String id, @RequestParam("selections") Optional<List<Integer>> selections, HttpServletResponse response) throws IOException {
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(Integer.valueOf(id));
        List<UaapGame> uaapGamesSelected = uaapDataService.findAllUaapGamesById(selections.get());
        List<UaapGameDto> uaapGameDtos = uaapDataService.getUaapGameDtos(uaapGamesSelected, uaapSeason.getGameCode().getGameCode());
        fileService.generateCSV(response, uaapSeason, uaapGameDtos);

    }

    //export to csv (ALL Uaap Games)
    @GetMapping("/export-to-csv")
    public void downloadUaapGame(HttpServletResponse response, @RequestParam("gameSeasonId") String id) throws IOException {
        fileService.generateCSV(response, uaapDataService.findUaapSeasonByIdSortedGames(Integer.parseInt(id)));
    }

    //image resource
    @GetMapping("/images/{resource}")
    public ResponseEntity<Resource> getResource(@PathVariable String resource) throws MalformedURLException {
        return fileService.getImageResource(resource);
    }

    //form (check url)
    @GetMapping("/check-url")
    public String checkUrl(@RequestParam("gameSeasonId") int id, Model model) {
        //find game
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(id);
        uaapDataService.checkUaapSeasonUrl(uaapSeason);
        model.addAttribute("gameSeason", uaapSeason);

        return "uaap-game-form";
    }
}
