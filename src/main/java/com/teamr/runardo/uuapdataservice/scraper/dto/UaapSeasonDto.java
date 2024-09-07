package com.teamr.runardo.uuapdataservice.scraper.dto;

import com.teamr.runardo.uuapdataservice.data.entity.UaapGameCode;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UaapSeasonDto {
    private int id;

    private int seasonNumber;

    private String url;

    private UaapGameCode gameCode;

    private boolean isUrlWorking;

    private List<UaapGameDto>  uaapGames;

    public UaapSeasonDto() {}

    public static UaapSeasonDto convertToDto(UaapSeason uaapSeason) {
        UaapSeasonDto seasonDto = new UaapSeasonDto();

        seasonDto.setId(uaapSeason.getId());
        seasonDto.setSeasonNumber(uaapSeason.getSeasonNumber());
        seasonDto.setUrl(uaapSeason.getUrl());
        seasonDto.setUrlWorking(uaapSeason.isUrlWork());

        if (uaapSeason.getUaapGames() != null) {
            seasonDto.setUaapGames(uaapSeason.getUaapGames().stream()
                    .map(UaapGameDto::convertToDto)
                    .toList()
            );
        }

        return seasonDto;
    }

    @Override
    public String toString() {
        return "UaapSeason{" +
                "id=" + id +
                ", seasonNumber=" + seasonNumber +
                ", url='" + url + '\'' +
                ", gameCode=" + gameCode +
                ", isUrlWorking=" + isUrlWorking +
                ", uaapGames=" + (Objects.isNull(uaapGames) ? 0 : uaapGames.size()) +
                '}';
    }


//    public static UaapSeason parse(String csvLine) {
//        String[] fields = csvLine.split(",\\s*");
//        UaapGameCode uaapGameCode = new UaapGameCode(fields[0], fields[1]);
//        return UaapSeason.builder()
//                .gameCode(uaapGameCode)
//                .seasonNumber(Integer.parseInt(fields[2]))
//                .url(fields[3])
//                .build();
//    }

}
