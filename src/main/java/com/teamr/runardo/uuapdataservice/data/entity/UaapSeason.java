package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name="uaap_seasons")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UaapSeason {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "season_number")
    private int seasonNumber;

    @Column(name = "url")
    private String url;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "game_code")
    private UaapGameCode gameCode;

    @Column(name = "is_url_working")
    private boolean urlWork;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "season_id")
    private List<UaapGame>  uaapGames;

    public UaapSeason() {}

    @Override
    public String toString() {
        return "UaapSeason{" +
                "id=" + id +
                ", seasonNumber=" + seasonNumber +
                ", url='" + url + '\'' +
                ", gameCode=" + gameCode +
                ", isUrlWorking=" + urlWork +
                ", uaapGames=" + (Objects.isNull(uaapGames) ? 0 : uaapGames.size()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UaapSeason that = (UaapSeason) o;
        return id == that.id && seasonNumber == that.seasonNumber && urlWork == that.urlWork && Objects.equals(url, that.url) && Objects.equals(gameCode, that.gameCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seasonNumber, url, gameCode, urlWork, uaapGames);
    }


    public static UaapSeason parse(String csvLine) {
        String[] fields = csvLine.split(",\\s*");
        UaapGameCode uaapGameCode = new UaapGameCode(fields[0], fields[1]);
        UaapSeason uaapSeason = new UaapSeason();
        return UaapSeason.builder()
                .gameCode(uaapGameCode)
                .seasonNumber(Integer.parseInt(fields[2]))
                .url(fields[3])
                .build();
    }

}
