package com.teamr.runardo.uuapdataservice.data.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "uaap_games")
@Setter
@Getter
@AllArgsConstructor
@Builder
public class UaapGame {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "game_number")
    private int gameNumber;

    @Column(name = "game_sched")
    private LocalDateTime gameSched;

    @Column(name = "game_venue")
    private String venue;

    @Column(name = "season_id")
    private int seasonId;

    @OneToMany(cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
    )
    @JoinColumn(name="game_id")
    private List<GameResult> gameResults;

    public UaapGame() {
    }
}
