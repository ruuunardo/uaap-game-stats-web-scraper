package com.teamr.runardo.uuapdataservice.scraper.filerepository;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Repository
@NoArgsConstructor
public class FileStorageRepository {
    @Value("${STORAGE_FOLDER}")
    private String storageFolder;

    public Resource findByName(String filename) {
        Path path = Path.of(storageFolder).resolve(filename).normalize();
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
