package com.teamr.runardo.uuapdataservice.scraper.service;

import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.data.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.data.dto.UaapSeasonDto;
import com.teamr.runardo.uuapdataservice.utility.CsvGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Service
@NoArgsConstructor
public class FileService {
    @Value("${STORAGE_FOLDER}")
    private String storageFolder;

//        image resource----------------------------------------------------
    public ResponseEntity<Resource> getImageResource(String resource) {
        String imgFile = resource;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename = \"%s\"", imgFile))
                .body(findByName(imgFile));
    }

    private Resource findByName(String filename) {
        Path path = Path.of(storageFolder).resolve(filename).normalize();
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

// get Uaap Games from file
    public List<UaapSeason> getUaapSeasonList(MultipartFile csvFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));

        return bufferedReader.lines()
                .skip(1)
                .map(UaapSeason::parse)
                .toList();
    }

    //        export CSV---------------------------------------------------------
    public void generateCSV(HttpServletResponse response, UaapSeason season) throws IOException {
        List<UaapGame> uaapGames = season.getUaapGames();
        List<UaapGameDto> uaapGameDtos = uaapGames.stream()
                .map(UaapGameDto::convertToDto)
                .toList();

        response.setContentType("text/csv");
        String header = String.format("attachment; filename=\"uaap-games_%s-%s.csv\"", season.getGameCode().getGameCode(), season.getSeasonNumber());
        response.addHeader("Content-Disposition", header);

        CsvGenerator csvGenerator = new CsvGenerator(response.getWriter());
        csvGenerator.writeUaapGamesToCsv(uaapGameDtos, UaapSeasonDto.convertToDto(season));
    }

    public void generateCSV(HttpServletResponse response, UaapSeason season, List<UaapGameDto> uaapGamesSelected) throws IOException {
        response.setContentType("text/csv");
        String header = String.format("attachment; filename=\"uaap-games_%s-%s_filtered.csv\"", season.getGameCode().getGameCode(), season.getSeasonNumber());
        response.addHeader("Content-Disposition", header);

        CsvGenerator csvGenerator = new CsvGenerator(response.getWriter());
        csvGenerator.writeUaapGamesToCsv(uaapGamesSelected, UaapSeasonDto.convertToDto(season), "STATS");
    }
}
