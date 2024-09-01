package com.teamr.runardo.uuapdataservice.view;

import com.teamr.runardo.uuapdataservice.data.entity.UaapGameCode;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.data.service.UaapDataService;
import com.teamr.runardo.uuapdataservice.filerepository.FileStorageRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    private FileStorageRepository fileStorageRepository;

    @Autowired
    public ViewController(FileStorageRepository fileStorageRepository, UaapDataService uaapDataService) {
        this.fileStorageRepository = fileStorageRepository;
        this.uaapDataService = uaapDataService;
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
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonById(id);
        model.addAttribute("gameSeason", uaapSeason);
        return "uaap-game-form";
    }

    //save season
    @PostMapping("/save")
    public String saveUaapGame(@Valid @ModelAttribute("gameSeason") UaapSeason uaapSeason, Errors errors) {
        if (!errors.hasErrors()) {
            uaapDataService.addUaapSeason(uaapSeason);
            return "redirect:/uaap-games";
        }
        return "uaap-games-form";
    }

    //save season from file
    @PostMapping(value = "/save-from-file")
    public String saveUaapGames(@RequestParam MultipartFile csvFile) {
        try {
            uaapDataService.importCSV(csvFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/uaap-games";
    }


    //Games and result
    @GetMapping("/gamelist/{gameSeasonId}")
    public String getGames(@PathVariable("gameSeasonId") int seasonId, Model model) {
        UaapSeason uaapSeason = uaapDataService.findUaapSeasonById(seasonId);
        model.addAttribute("uaapSeason", uaapSeason);
        return "uaap-game-table";
    }

    //Delete games and result
    @PostMapping(value = "/gamelist/{gameSeasonId}", params = "delete=true")
    public String deleteGames(@PathVariable("gameSeasonId") int seasonId, @RequestParam("selections") Optional<List<Integer>> selections) {
        uaapDataService.deleteUaapGames(selections);
        String path = "redirect:gameSeasonId";
        return path.replace("gameSeasonId", String.valueOf(seasonId));
    }

    //image source
    @GetMapping("/images/{resource}")
    public ResponseEntity<Resource> getResource(@PathVariable String resource) throws MalformedURLException {
        return uaapDataService.getImageResource(resource);
    }

    private ResponseEntity<Resource> getImageResource(String resource) {
        String imgFile = resource.concat(".png");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename = \"%s\"", imgFile))
                .body(fileStorageRepository.findByName(imgFile));
    }

    //export to csv (ALL Uaap Games)
    @GetMapping("/export-to-csv")
    public void downloadUaapGame(HttpServletResponse response, @RequestParam("gameSeasonId") String id) throws IOException {
        uaapDataService.generateCSV(response, id);
    }

    //export to csv (selected Uaap Games)
    @PostMapping(value = "/gamelist/{gameSeasonId}", params = "download=true")
    public void downloadGameList(@PathVariable("gameSeasonId") String id, @RequestParam("selections") Optional<List<Integer>> selections, HttpServletResponse response) throws IOException {
        uaapDataService.generateCSV(response, id, selections);
    }

}
