package com.teamr.runardo.uuapdataservice.scraper.gamescraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class UtilityClass {
    public static Document getGameDocument(String url) throws IOException {
        Document doc;
            doc = Jsoup.connect(url).get();
        return doc;
    }

    public static Document getGameDocument(File url) throws IOException {
        Document doc;
            doc = Jsoup.parse(url);
        return doc;
    }
}
