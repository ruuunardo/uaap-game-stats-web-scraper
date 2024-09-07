package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import jdk.dynalink.linker.LinkerServices;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "uaap_game_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResult {
    @Id //<gameID-univCode>
    @Column(name = "id")
    private String id;

    @Column(name = "game_id")
    private int gameId;

    @OneToOne()
    @JoinColumn(name = "univ_id")
    private UaapUniv univ;

    @Column(name = "team_tag")
    private String teamTag;

    @Column(name = "final_score")
    private int finalScore;

    @Override
    public String toString() {
        return "GameResult{" +
                "id='" + id + '\'' +
                ", gameId=" + gameId +
                ", univ=" + univ +
                ", teamTag='" + teamTag + '\'' +
                ", finalScore=" + finalScore +
                '}';
    }
}
