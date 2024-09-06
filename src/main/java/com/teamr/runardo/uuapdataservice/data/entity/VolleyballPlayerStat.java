package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volleyball_player_stats")
@IdClass(CompositeStatId.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolleyballPlayerStat implements PlayerStat {
    @Id
    @JoinColumn(name = "player_id")
    @ManyToOne()
    private Player player;

    @Id
    @Column(name = "game_result_id")
    private String gameResult;

    @Column(name = "attack_attempt")
    private Integer attackAttempt;

    @Column(name = "attack_made")
    private Integer attackMade;

    @Override
    public String toString() {
        return "VolleyballPlayerStat{" +
                "player=" + player +
                ", gameResult='" + gameResult + '\'' +
                ", attackAttempt=" + attackAttempt +
                ", attackMade=" + attackMade +
                '}';
    }
}
