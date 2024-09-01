package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "uaap_game_codes")
@Setter
@Getter
@NoArgsConstructor
public class UaapGameCode {
    @Id
    @Column(name = "game_code")
    private String gameCode;

    @Column(name = "game_name")
    private String gameName;


    public UaapGameCode(String gameCode, String gameName) {
        this.gameName = gameName;
        this.gameCode = gameCode;
    }

    @Override
    public String toString() {
        return "UaapGameCode{" +
                "gameCode='" + gameCode + '\'' +
                ", gameName='" + gameName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UaapGameCode gameCode1 = (UaapGameCode) o;
        return Objects.equals(gameCode, gameCode1.gameCode) && Objects.equals(gameName, gameCode1.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameCode, gameName);
    }
}
