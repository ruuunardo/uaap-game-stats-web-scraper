package com.teamr.runardo.uuapdataservice.scraper.service;

import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import com.teamr.runardo.uuapdataservice.scraper.filerepository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class FileService {
    private FileStorageRepository fileStorageRepository;

    @Autowired
    public FileService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
    }

//        image resource----------------------------------------------------
    public ResponseEntity<Resource> getImageResource(String resource) {
        String imgFile = resource.concat(".png");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename = \"%s\"", imgFile))
                .body(fileStorageRepository.findByName(imgFile));
    }

    public List<UaapSeason> getUaapSeasonList(MultipartFile csvFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));

        return bufferedReader.lines()
                .skip(1)
                .map(UaapSeason::parse)
                .toList();
    }
}
