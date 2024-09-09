package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "basketball_player_stats")
@IdClass(CompositeStatId.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketballPlayerStat implements PlayerStat{
        @Id
        @JoinColumn(name = "player_id")
        @ManyToOne(cascade = CascadeType.MERGE)
        private Player player;

        @Id
        @Column(name = "game_result_id")
        private String gameResult;

        @Column(name = "is_first_five")
        private Integer isFirstFive;

        @Column(name = "points")
        private Integer points;

        @Override
        public String toString() {
                return "BasketballPlayerStat{" +
                        "player=" + player +
                        ", gameResult='" + gameResult + '\'' +
                        ", isFirstFive=" + isFirstFive +
                        ", points=" + points +
                        '}';
        }
}
