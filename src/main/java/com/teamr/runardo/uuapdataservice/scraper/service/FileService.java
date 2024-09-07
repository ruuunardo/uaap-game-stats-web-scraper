package com.teamr.runardo.uuapdataservice.scraper.service;

import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.scraper.filerepository.FileStorageRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
//        String imgFile = resource.concat(".png");
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


}
