package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "uaap_players")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Player {
    @Id//<univCode-jerseyNum>
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "univ_id")
    private int univId;

    public Player() {
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", univId=" + univId +
                '}';
    }
}
