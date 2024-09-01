package com.teamr.runardo.uuapdataservice.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CompositeStatId implements Serializable {
    private Player player;

    private String gameResult;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeStatId that = (CompositeStatId) o;
        return Objects.equals(player, that.player) && Objects.equals(gameResult, that.gameResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, gameResult);
    }
}
