package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

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

        @Column(name = "min_played")
        private LocalTime minPlayed;

        @Column(name = "field_goal_made")
        private Integer fieldGoalMade;

        @Column(name = "field_goal_attempt")
        private Integer fieldGoalAttempts;

        @Column(name = "two_points_made")
        private Integer twoPointsMade;

        @Column(name = "two_points_attempt")
        private Integer twoPointsAttempts;

        @Column(name = "three_points_made")
        private Integer threePointsMade;

        @Column(name = "three_points_attempt")
        private Integer threePointsAttempts;

        @Column(name = "free_throw_made")
        private Integer freeThrowMade;

        @Column(name = "free_throw_attempt")
        private Integer freeThrowAttempts;

        @Column(name = "rebound_or")
        private Integer reboundsOR;

        @Column(name = "rebound_dr")
        private Integer reboundsDR;

        @Column(name = "assist")
        private Integer assist;

        @Column(name = "turn_over")
        private Integer turnOver;

        @Column(name = "steal")
        private Integer steal;

        @Column(name = "block")
        private Integer block;

        @Column(name = "foul_pf")
        private Integer foulsPF;

        @Column(name = "foul_fd")
        private Integer foulsFD;

        @Column(name = "efficiency")
        private Integer efficiency;



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
